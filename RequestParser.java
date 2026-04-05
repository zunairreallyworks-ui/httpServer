public class RequestParser {

    private String requestLine;

    public RequestParser(String requestLine) {
        this.requestLine = requestLine;
    }

    public HttpRequest parse() {
        if (requestLine == null || requestLine.trim().isEmpty()) {
            return null;
        }

        String[] parts = requestLine.trim().split("\\s+");

        if (parts.length != 3) {
            return null;
        }

        String method = parts[0];
        String path = parts[1];
        String version = parts[2];

        if (method.trim().isEmpty()) {
            return null;
        }

        if (path.trim().isEmpty()) {
            return null;
        }

        if (!version.startsWith("HTTP/")) {
            return null;
        }

        if (!method.equals("GET")) {
            return null;
        }

        if (!version.equals("HTTP/1.1")) {
            return null;
        }

        return new HttpRequest(method, path, version);
    }
}