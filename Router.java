public class Router {

    private final StaticFileService staticFileService;
    private final FormService formService;

    public Router() {
        this.staticFileService = new StaticFileService();
        this.formService = new FormService();
    }

    public HttpResponse route(HttpRequest request) {
        if (request == null) {
            return createErrorResponse(400, "Bad Request", "Invalid request");
        }

        String method = request.getMethod();
        String path = request.getPath();

        if (method == null || path == null) {
            return createErrorResponse(400, "Bad Request", "Missing method or path");
        }

        AppLogger.logRequest(method, path);

        if ("GET".equals(method)) {
            if ("/".equals(path)) {
                path = "/index.html";
            }

            if ("/submit".equals(path)) {
                return formService.handleGet(request);
            }

            return staticFileService.handleGet(path);
        }

        if ("POST".equals(method)) {
            if ("/submit".equals(path)) {
                return formService.handlePost(request);
            }

            AppLogger.logNotFound(path);
            return createErrorResponse(404, "Not Found", "POST route not found");
        }

        AppLogger.warning("405 Method Not Allowed: " + method + " " + path);
        return createErrorResponse(405, "Method Not Allowed", "Method not allowed");
    }

    private HttpResponse createErrorResponse(int statusCode, String reasonPhrase, String body) {
        HttpResponse response = new HttpResponse("HTTP/1.1", statusCode, reasonPhrase, body);
        response.addHeader("Content-Type", "text/plain; charset=UTF-8");
        return response;
    }
}