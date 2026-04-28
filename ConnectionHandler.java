import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class ConnectionHandler implements Runnable {
    // Each handler instance is responsible for one client socket connection.
    private final Socket socket;

    // Store the connected client socket so the request can be read and responded to.
    public ConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    // Called when this handler is executed by a thread.
    // It delegates the actual work to the handle method.
    @Override
    public void run() {
        handle();
    }

    // Handles the full lifecycle of a single client request:
    // logging the connection, reading the HTTP request,
    // routing it, sending the response, and closing the socket.
    public void handle() {
        AppLogger.logClientConnected(socket.toString());

        try (
            // Reader for incoming text data from the client socket.
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            );
            // Writer for sending the HTTP response back to the client.
            BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream())
            )
        ) {
            HttpResponse response;

            try {
                // Read the first line of the HTTP request, such as:
                // GET /index.html HTTP/1.1
                String requestLine = reader.readLine();
                System.out.println("REQUEST LINE = [" + requestLine + "]");
                if (requestLine == null || requestLine.trim().isEmpty()) {
                    throw new IllegalArgumentException("Request line was empty");
                }

                // Build the full raw request so it can be passed to the parser later.
                StringBuilder rawRequest = new StringBuilder();
                rawRequest.append(requestLine).append("\r\n");

                String line;
                int contentLength = 0;
                boolean blankLineFound = false;

                // Read request headers line by line until the blank line is found.
                while ((line = reader.readLine()) != null) {
                    rawRequest.append(line).append("\r\n");

                    // Look for the Content-Length header so the body size can be read correctly.
                    if (line.toLowerCase().startsWith("content-length:")) {
                        String value = line.substring(line.indexOf(":") + 1).trim();

                        try {
                            contentLength = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid Content-Length header");
                        }

                        // Negative body lengths are invalid and should be rejected.
                        if (contentLength < 0) {
                            throw new IllegalArgumentException("Content-Length cannot be negative");
                        }
                    }

                    // A blank line marks the end of the headers section.
                    if (line.isEmpty()) {
                        blankLineFound = true;
                        break;
                    }
                }

                // If no blank line was found, the request format is incomplete.
                if (!blankLineFound) {
                    throw new IllegalArgumentException("Missing blank line after headers");
                }

                // If the request includes a body, read exactly the number of characters declared.
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

                    // Reject the request if the body received is shorter than expected.
                    if (totalRead < contentLength) {
                        throw new IllegalArgumentException("Request body was shorter than Content-Length");
                    }

                    // Append the body to the raw request so the parser receives the full message.
                    rawRequest.append(bodyChars, 0, totalRead);
                }

                // Parse the raw HTTP request into a structured HttpRequest object.
                RequestParser parser = new RequestParser(rawRequest.toString());
                HttpRequest request = parser.parse();

                // Pass the parsed request to the router to decide how it should be handled.
                Router router = new Router();
                response = router.route(request);

                // As a safety fallback, generate a 500 response if the router returns nothing.
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
                // Handle malformed client requests with a 400 Bad Request response.
                AppLogger.logMalformedRequest(e.getMessage());

                response = new HttpResponse(
                    "HTTP/1.1",
                    400,
                    "Bad Request",
                    e.getMessage()
                );
                response.addHeader("Content-Type", "text/plain; charset=UTF-8");

            } catch (Exception e) {
                // Catch unexpected failures and return a controlled 500 response.
                AppLogger.logInternalError("Unexpected failure in ConnectionHandler", e);
                e.printStackTrace();

                response = new HttpResponse(
                    "HTTP/1.1",
                    500,
                    "Internal Server Error",
                    "An internal server error occurred"
                );
                response.addHeader("Content-Type", "text/plain; charset=UTF-8");
            }

            // Send the completed HTTP response to the client.
            writer.write(response.buildResponse());
            writer.flush();

        } catch (IOException e) {
            // Log any low-level I/O errors that happen while reading or writing.
            AppLogger.logInternalError("I/O failure in ConnectionHandler", e);
            e.printStackTrace();
        } finally {
            try {
                // Always close the socket after the request is handled.
                socket.close();
            } catch (IOException e) {
                // Log any failure that occurs while closing the socket.
                AppLogger.logInternalError("Failed to close socket", e);
                e.printStackTrace();
            }
        }
    }
}