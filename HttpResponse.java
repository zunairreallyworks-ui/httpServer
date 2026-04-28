import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    // Stores the HTTP version for the response, such as HTTP/1.1.
    private final String httpVersion;

    // Stores the numeric status code, such as 200 or 404.
    private final int statusCode;

    // Stores the textual reason phrase, such as OK or Not Found.
    private final String reasonPhrase;

    // Stores the response headers in insertion order.
    private final Map<String, String> headers;

    // Stores the response body content.
    private final String body;

    // Creates a new HttpResponse object with the basic response information.
    public HttpResponse(String httpVersion, int statusCode, String reasonPhrase, String body) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.body = (body == null) ? "" : body;
        this.headers = new LinkedHashMap<>();
    }

    // Adds a header to the response.
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    // Builds and returns the status line, for example:
    // HTTP/1.1 200 OK
    public String getStatusLine() {
        return httpVersion + " " + statusCode + " " + reasonPhrase;
    }

    // Builds the full HTTP response as a single string,
    // including the status line, headers, blank line, and body.
    public String buildResponse() {
        // Add Content-Length automatically if it has not already been set.
        if (!headers.containsKey("Content-Length")) {
            headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        }

        StringBuilder response = new StringBuilder();

        // Start with the HTTP status line.
        response.append(getStatusLine()).append("\r\n");

        // Append each header on its own line.
        for (Map.Entry<String, String> header : headers.entrySet()) {
            response.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append("\r\n");
        }

        // Add the required blank line between headers and body.
        response.append("\r\n");
        response.append(body);

        return response.toString();
    }
}