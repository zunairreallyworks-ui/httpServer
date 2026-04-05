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
            String requestLine = reader.readLine();

            if (requestLine == null || requestLine.trim().isEmpty()) {
                return;
            }

            StringBuilder rawRequest = new StringBuilder();
            rawRequest.append(requestLine).append("\r\n");

            String line;
            int contentLength = 0;

            while ((line = reader.readLine()) != null) {
                rawRequest.append(line).append("\r\n");

                if (line.toLowerCase().startsWith("content-length:")) {
                    String value = line.substring("Content-Length:".length()).trim();
                    contentLength = Integer.parseInt(value);
                }

                if (line.isEmpty()) {
                    break;
                }
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

                rawRequest.append(bodyChars, 0, totalRead);
            }

            RequestParser parser = new RequestParser(rawRequest.toString());
            HttpRequest request = parser.parse();

            Router router = new Router();
            HttpResponse response = router.route(request);

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