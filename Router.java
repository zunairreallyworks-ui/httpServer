public class Router {

    // Handles requests for static files such as HTML pages.
    private final StaticFileService staticFileService;

    // Handles form submission routes.
    private final FormService formService;

    // Create the router and initialise the services it delegates to.
    public Router() {
        this.staticFileService = new StaticFileService();
        this.formService = new FormService();
    }

    // Routes an incoming HttpRequest to the correct handler based on method and path.
    public HttpResponse route(HttpRequest request) {
        // Reject null requests immediately.
        if (request == null) {
            return createErrorResponse(400, "Bad Request", "Invalid request");
        }

        String method = request.getMethod();
        String path = request.getPath();

        // Reject requests missing essential request line information.
        if (method == null || path == null) {
            return createErrorResponse(400, "Bad Request", "Missing method or path");
        }

        // Log the incoming request before routing it.
        AppLogger.logRequest(method, path);

        // Handle GET requests.
        if ("GET".equals(method)) {
            // Map the root path to the default homepage.
            if ("/".equals(path)) {
                path = "/index.html";
            }

            // Send GET requests for /submit to the form service.
            if ("/submit".equals(path)) {
                return formService.handleGet(request);
            }

            // All other GET requests are treated as static file requests.
            return staticFileService.handleGet(path);
        }

        // Handle POST requests.
        if ("POST".equals(method)) {
            // Only the /submit route is supported for POST.
            if ("/submit".equals(path)) {
                return formService.handlePost(request);
            }

            // Any other POST path is treated as missing.
            AppLogger.logNotFound(path);
            return createErrorResponse(404, "Not Found", "POST route not found");
        }

        // Any other HTTP method is rejected.
        AppLogger.warning("405 Method Not Allowed: " + method + " " + path);
        return createErrorResponse(405, "Method Not Allowed", "Method not allowed");
    }

    // Helper method used to create plain text error responses.
    private HttpResponse createErrorResponse(int statusCode, String reasonPhrase, String body) {
        HttpResponse response = new HttpResponse("HTTP/1.1", statusCode, reasonPhrase, body);
        response.addHeader("Content-Type", "text/plain; charset=UTF-8");
        return response;
    }
}