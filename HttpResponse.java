package server;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private String httpVersion;
    private int statusCode;
    private String reasonPhrase;
    private Map<String, String> headers;
    private String body;

    public HttpResponse(String httpVersion, int statusCode, String reasonPhrase, String body) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.body = (body == null) ? "" : body;
        this.headers = new LinkedHashMap<>();
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public String getStatusLine() {
        return httpVersion + " " + statusCode + " " + reasonPhrase;
    }

    public String buildResponse() {
        if (!headers.containsKey("Content-Length")) {
            headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        }

        StringBuilder response = new StringBuilder();

        response.append(getStatusLine()).append("\r\n");

        for (Map.Entry<String, String> header : headers.entrySet()) {
            response.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append("\r\n");
        }

        response.append("\r\n");
        response.append(body);

        return response.toString();
    }
}