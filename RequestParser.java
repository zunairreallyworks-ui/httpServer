import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestParser {

    private final String rawRequest;

    public RequestParser(String rawRequest) {
        this.rawRequest = rawRequest;
    }

    public HttpRequest parse() {
        if (rawRequest == null || rawRequest.trim().isEmpty()) {
            AppLogger.logMalformedRequest("Request was empty");
            throw new IllegalArgumentException("Request was empty");
        }

        String[] lines = rawRequest.split("\\r?\\n");

        if (lines.length == 0) {
            AppLogger.logMalformedRequest("Request had no lines");
            throw new IllegalArgumentException("Request had no lines");
        }

        String requestLine = lines[0].trim();

        if (requestLine.isEmpty()) {
            AppLogger.logMalformedRequest("Request line was empty");
            throw new IllegalArgumentException("Request line was empty");
        }

        String[] parts = requestLine.split("\\s+");

        if (parts.length != 3) {
            AppLogger.logMalformedRequest("Malformed request line");
            throw new IllegalArgumentException("Malformed request line");
        }

        String method = parts[0];
        String path = parts[1];
        String version = parts[2];

        if (method.trim().isEmpty()) {
            AppLogger.logMalformedRequest("HTTP method was empty");
            throw new IllegalArgumentException("HTTP method was empty");
        }

        if (path.trim().isEmpty()) {
            AppLogger.logMalformedRequest("Request path was empty");
            throw new IllegalArgumentException("Request path was empty");
        }

        if (!version.startsWith("HTTP/")) {
            AppLogger.logMalformedRequest("Invalid HTTP version format");
            throw new IllegalArgumentException("Invalid HTTP version format");
        }

        if (!version.equals("HTTP/1.1")) {
            AppLogger.logMalformedRequest("Only HTTP/1.1 is supported");
            throw new IllegalArgumentException("Only HTTP/1.1 is supported");
        }

        String queryString = "";
        Map<String, String> parameters = new HashMap<>();

        if (path.contains("?")) {
            String[] fullPath = path.split("\\?", 2);
            path = fullPath[0];
            queryString = fullPath[1];

            String[] pairs = queryString.split("&");

            for (String pair : pairs) {
                if (pair.trim().isEmpty()) {
                    continue;
                }

                String[] keyValue = pair.split("=", 2);
                String key = keyValue[0].trim();
                String value = keyValue.length > 1 ? keyValue[1].trim() : "";

                if (!key.isEmpty()) {
                    parameters.put(key, value);
                }
            }
        }

        Map<String, String> headers = new LinkedHashMap<>();
        int bodyStartIndex = -1;

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];

            if (line.trim().isEmpty()) {
                bodyStartIndex = i + 1;
                break;
            }

            int colonIndex = line.indexOf(":");

            if (colonIndex <= 0) {
                AppLogger.logMalformedRequest("Invalid header format");
                throw new IllegalArgumentException("Invalid header format");
            }

            String headerName = line.substring(0, colonIndex).trim();
            String headerValue = line.substring(colonIndex + 1).trim();

            if (headerName.isEmpty()) {
                AppLogger.logMalformedRequest("Header name was empty");
                throw new IllegalArgumentException("Header name was empty");
            }

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

        if (bodyStartIndex != -1 && bodyStartIndex < lines.length) {
            for (int i = bodyStartIndex; i < lines.length; i++) {
                bodyBuilder.append(lines[i]);

                if (i < lines.length - 1) {
                    bodyBuilder.append("\n");
                }
            }
        }

        String body = bodyBuilder.toString();

        return new HttpRequest(method, path, version, headers, body, queryString, parameters);
    }
}