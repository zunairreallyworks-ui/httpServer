public class Router {

    private final StaticFileService staticFileService;

    public Router() {
        this.staticFileService = new StaticFileService();
    }

    public HttpResponse route(HttpRequest request) {
        if (request == null) {
            return createErrorResponse(400, "Bad Request", "Invalid request");
        }

        String method = request.getMethod();
        String path = request.getPath();

        if ("GET".equals(method)) {
            return staticFileService.handleGet(path);
        }

        return createErrorResponse(405, "Method Not Allowed", "Method not allowed");
    }

    private HttpResponse createErrorResponse(int statusCode, String reasonPhrase, String body) {
        HttpResponse response = new HttpResponse("HTTP/1.1", statusCode, reasonPhrase, body);
        response.addHeader("Content-Type", "text/plain");
        return response;
    }
}