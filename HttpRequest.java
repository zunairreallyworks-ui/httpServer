import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private String version;
    private Map<String, String> headers;
    private String body;
    private String queryString;
    private Map<String, String> parameters;

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

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String getQueryString() {
        return queryString;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}