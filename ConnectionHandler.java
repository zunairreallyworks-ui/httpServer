import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class ConnectionHandler {
    private Socket socket;

    public ConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    public void handle() {
        System.out.println("Connected: " + socket);

        try (
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            );
            BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream())
            )
        ) {
            HttpResponse response;

            try {
                String requestLine = reader.readLine();

                if (requestLine == null || requestLine.trim().isEmpty()) {
                    throw new IllegalArgumentException("Request line was empty");
                }

                StringBuilder rawRequest = new StringBuilder();
                rawRequest.append(requestLine).append("\r\n");

                String line;
                int contentLength = 0;
                boolean blankLineFound = false;

                while ((line = reader.readLine()) != null) {
                    rawRequest.append(line).append("\r\n");

                    if (line.toLowerCase().startsWith("content-length:")) {
                        String value = line.substring(line.indexOf(":") + 1).trim();

                        try {
                            contentLength = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid Content-Length header");
                        }

                        if (contentLength < 0) {
                            throw new IllegalArgumentException("Content-Length cannot be negative");
                        }
                    }

                    if (line.isEmpty()) {
                        blankLineFound = true;
                        break;
                    }
                }

                if (!blankLineFound) {
                    throw new IllegalArgumentException("Missing blank line after headers");
                }

                if (contentLength > 0) {
                    char[] bodyChars = new char[contentLength];
                    int totalRead = 0;

                    while (totalRead < contentLength) {
                        int charsRead = reader.read(bodyChars, totalRead, contentLength - totalRead);

                        if (charsRead == -1) {
                            break;
                        }

                        totalRead += charsRead;
                    }

                    if (totalRead < contentLength) {
                        throw new IllegalArgumentException("Request body was shorter than Content-Length");
                    }

                    rawRequest.append(bodyChars, 0, totalRead);
                }

                RequestParser parser = new RequestParser(rawRequest.toString());
                HttpRequest request = parser.parse();

                Router router = new Router();
                response = router.route(request);

                if (response == null) {
                    response = new HttpResponse(
                        "HTTP/1.1",
                        500,
                        "Internal Server Error",
                        "Router returned no response"
                    );
                    response.addHeader("Content-Type", "text/plain; charset=UTF-8");
                }

            } catch (IllegalArgumentException e) {
                response = new HttpResponse(
                    "HTTP/1.1",
                    400,
                    "Bad Request",
                    e.getMessage()
                );
                response.addHeader("Content-Type", "text/plain; charset=UTF-8");

            } catch (Exception e) {
                e.printStackTrace();

                response = new HttpResponse(
                    "HTTP/1.1",
                    500,
                    "Internal Server Error",
                    "An internal server error occurred"
                );
                response.addHeader("Content-Type", "text/plain; charset=UTF-8");
            }

            writer.write(response.buildResponse());
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}