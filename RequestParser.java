import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestParser {

    // Stores the raw HTTP request text received from the client.
    private final String rawRequest;

    // Creates a parser instance for the given raw request.
    public RequestParser(String rawRequest) {
        this.rawRequest = rawRequest;
    }

    // Parses the raw request string into a structured HttpRequest object.
    public HttpRequest parse() {
        // Reject completely empty requests.
        if (rawRequest == null || rawRequest.trim().isEmpty()) {
            AppLogger.logMalformedRequest("Request was empty");
            throw new IllegalArgumentException("Request was empty");
        }

        // Split the request into lines so the request line, headers, and body can be processed.
        String[] lines = rawRequest.split("\\r?\\n");

        if (lines.length == 0) {
            AppLogger.logMalformedRequest("Request had no lines");
            throw new IllegalArgumentException("Request had no lines");
        }

        // The first line should always be the HTTP request line.
        String requestLine = lines[0].trim();

        if (requestLine.isEmpty()) {
            AppLogger.logMalformedRequest("Request line was empty");
            throw new IllegalArgumentException("Request line was empty");
        }

        // Split the request line into method, path, and HTTP version.
        String[] parts = requestLine.split("\\s+");

        if (parts.length != 3) {
            AppLogger.logMalformedRequest("Malformed request line");
            throw new IllegalArgumentException("Malformed request line");
        }

        String method = parts[0];
        String path = parts[1];
        String version = parts[2];

        // Ensure the HTTP method is present.
        if (method.trim().isEmpty()) {
            AppLogger.logMalformedRequest("HTTP method was empty");
            throw new IllegalArgumentException("HTTP method was empty");
        }

        // Ensure the request path is present.
        if (path.trim().isEmpty()) {
            AppLogger.logMalformedRequest("Request path was empty");
            throw new IllegalArgumentException("Request path was empty");
        }

        // Only HTTP versions in the expected format are accepted.
        if (!version.startsWith("HTTP/")) {
            AppLogger.logMalformedRequest("Invalid HTTP version format");
            throw new IllegalArgumentException("Invalid HTTP version format");
        }

        // This server is limited to HTTP/1.1 requests.
        if (!version.equals("HTTP/1.1")) {
            AppLogger.logMalformedRequest("Only HTTP/1.1 is supported");
            throw new IllegalArgumentException("Only HTTP/1.1 is supported");
        }

        String queryString = "";
        Map<String, String> parameters = new HashMap<>();

        // If the path contains a query string, split it away from the main path.
        if (path.contains("?")) {
            String[] fullPath = path.split("\\?", 2);
            path = fullPath[0];
            queryString = fullPath[1];

            // Split the query string into individual key-value pairs.
            String[] pairs = queryString.split("&");

            for (String pair : pairs) {
                if (pair.trim().isEmpty()) {
                    continue;
                }

                String[] keyValue = pair.split("=", 2);
                String key = keyValue[0].trim();
                String value = keyValue.length > 1 ? keyValue[1].trim() : "";

                // Only store parameters with a non-empty key.
                if (!key.isEmpty()) {
                    parameters.put(key, value);
                }
            }
        }

        // Store headers in insertion order.
        Map<String, String> headers = new LinkedHashMap<>();
        int bodyStartIndex = -1;

        // Process all lines after the request line as headers until a blank line is found.
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];

            // A blank line marks the end of the headers and the start of the body.
            if (line.trim().isEmpty()) {
                bodyStartIndex = i + 1;
                break;
            }

            int colonIndex = line.indexOf(":");

            // Headers must contain a colon separating the name and value.
            if (colonIndex <= 0) {
                AppLogger.logMalformedRequest("Invalid header format");
                throw new IllegalArgumentException("Invalid header format");
            }

            String headerName = line.substring(0, colonIndex).trim();
            String headerValue = line.substring(colonIndex + 1).trim();

            // Reject headers with an empty name.
            if (headerName.isEmpty()) {
                AppLogger.logMalformedRequest("Header name was empty");
                throw new IllegalArgumentException("Header name was empty");
            }

            // Validate Content-Length if it is present.
            if (headerName.equalsIgnoreCase("Content-Length")) {
                if (headerValue.isEmpty()) {
                    AppLogger.logMalformedRequest("Content-Length header was empty");
                    throw new IllegalArgumentException("Content-Length header was empty");
                }

                try {
                    int length = Integer.parseInt(headerValue);

                    if (length < 0) {
                        AppLogger.logMalformedRequest("Content-Length cannot be negative");
                        throw new IllegalArgumentException("Content-Length cannot be negative");
                    }
                } catch (NumberFormatException e) {
                    AppLogger.logMalformedRequest("Invalid Content-Length value");
                    throw new IllegalArgumentException("Invalid Content-Length value");
                }
            }

            headers.put(headerName, headerValue);
        }

        StringBuilder bodyBuilder = new StringBuilder();

        // Rebuild the request body from all lines after the blank line.
        if (bodyStartIndex != -1 && bodyStartIndex < lines.length) {
            for (int i = bodyStartIndex; i < lines.length; i++) {
                bodyBuilder.append(lines[i]);

                if (i < lines.length - 1) {
                    bodyBuilder.append("\n");
                }
            }
        }

        String body = bodyBuilder.toString();

        // Return a structured HttpRequest containing all parsed parts of the request.
        return new HttpRequest(method, path, version, headers, body, queryString, parameters);
    }
}