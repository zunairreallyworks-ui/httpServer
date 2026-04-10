import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestParser {

    private String rawRequest;

    public RequestParser(String rawRequest) {
        this.rawRequest = rawRequest;
    }

    public HttpRequest parse() {
        if (rawRequest == null || rawRequest.trim().isEmpty()) {
            throw new IllegalArgumentException("Request was empty");
        }

        String[] lines = rawRequest.split("\\r?\\n");

        if (lines.length == 0) {
            throw new IllegalArgumentException("Request had no lines");
        }

        String requestLine = lines[0].trim();

        if (requestLine.isEmpty()) {
            throw new IllegalArgumentException("Request line was empty");
        }

        String[] parts = requestLine.split("\\s+");

        if (parts.length != 3) {
            throw new IllegalArgumentException("Malformed request line");
        }

        String method = parts[0];
        String path = parts[1];
        String version = parts[2];

        if (method.trim().isEmpty()) {
            throw new IllegalArgumentException("HTTP method was empty");
        }

        if (path.trim().isEmpty()) {
            throw new IllegalArgumentException("Request path was empty");
        }

        if (!version.startsWith("HTTP/")) {
            throw new IllegalArgumentException("Invalid HTTP version format");
        }

        if (!version.equals("HTTP/1.1")) {
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
        throw new IllegalArgumentException("Invalid header format");
    }

    String headerName = line.substring(0, colonIndex).trim();
    String headerValue = line.substring(colonIndex + 1).trim();

    if (headerName.isEmpty()) {
        throw new IllegalArgumentException("Header name was empty");
    }

    if (headerName.equalsIgnoreCase("Content-Length")) {
        if (headerValue.isEmpty()) {
            throw new IllegalArgumentException("Content-Length header was empty");
        }

        try {
            int length = Integer.parseInt(headerValue);

            if (length < 0) {
                throw new IllegalArgumentException("Content-Length cannot be negative");
            }
        } catch (NumberFormatException e) {
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