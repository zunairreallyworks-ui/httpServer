import java.util.Map;

public class HttpRequest {
    // Stores the HTTP method, such as GET or POST.
    private String method;

    // Stores the requested path, such as /index.html.
    private String path;

    // Stores the HTTP version from the request line.
    private String version;

    // Stores all request headers as key-value pairs.
    private Map<String, String> headers;

    // Stores the request body, mainly used for POST requests.
    private String body;

    // Stores the raw query string portion of the URL, if present.
    private String queryString;

    // Stores parsed query parameters as key-value pairs.
    private Map<String, String> parameters;

    // Creates a new HttpRequest object containing all parsed request data.
    public HttpRequest(String method, String path, String version,
                       Map<String, String> headers, String body,
                       String queryString, Map<String, String> parameters) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.body = body;
        this.queryString = queryString;
        this.parameters = parameters;
    }

    // Returns the HTTP method of the request.
    public String getMethod() {
        return method;
    }

    // Returns the requested path.
    public String getPath() {
        return path;
    }

    // Returns the HTTP version.
    public String getVersion() {
        return version;
    }

    // Returns all request headers.
    public Map<String, String> getHeaders() {
        return headers;
    }

    // Returns the request body.
    public String getBody() {
        return body;
    }

    // Returns the raw query string.
    public String getQueryString() {
        return queryString;
    }

    // Returns the parsed request parameters.
    public Map<String, String> getParameters() {
        return parameters;
    }
}