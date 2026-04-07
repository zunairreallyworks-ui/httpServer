import java.util.HashMap;
import java.util.Map;

public class FormService {

    private final SubmissionStore submissionStore;

    public FormService() {
        this.submissionStore = new SubmissionStore();
    }
    private HttpResponse handleFormData(Map<String, String> formData) {
    if (formData == null || formData.isEmpty()) {
        return createResponse(400, "Bad Request", "Form data is empty");
    }

    String name = getSafeValue(formData.get("name"));
    String email = getSafeValue(formData.get("email"));
    String message = getSafeValue(formData.get("message"));

    if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
        return createResponse(400, "Bad Request", "All fields are required");
    }

    Map<String, String> submissionData = new HashMap<>();
    submissionData.put("name", name);
    submissionData.put("email", email);
    submissionData.put("message", message);

    boolean saved = submissionStore.saveSubmission(submissionData);

    if (!saved) {
        return createResponse(500, "Internal Server Error", "Could not save submission");
    }

    return createResponse(200, "OK", "Form submitted successfully");
}
    public HttpResponse handlePost(HttpRequest request) {
    if (request == null) {
        return createResponse(400, "Bad Request", "Request was null");
    }

    String body = request.getBody();

    if (body == null || body.trim().isEmpty()) {
        return createResponse(400, "Bad Request", "Form body is empty");
    }

    Map<String, String> formData = parseFormData(body);

    return handleFormData(formData);
}
public HttpResponse handleGet(HttpRequest request) {
    if (request == null) {
        return createResponse(400, "Bad Request", "Request was null");
    }

    Map<String, String> formData = request.getParameters();

    return handleFormData(formData);
}
    private Map<String, String> parseFormData(String body) {
        Map<String, String> formData = new HashMap<>();

        String[] pairs = body.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);

            if (keyValue.length == 2) {
                String key = decodeFormValue(keyValue[0]);
                String value = decodeFormValue(keyValue[1]);
                formData.put(key, value);
            }
        }

        return formData;
    }

    private String decodeFormValue(String value) {
        value = value.replace("+", " ");
        value = value.replace("%40", "@");
        value = value.replace("%2E", ".");
        value = value.replace("%21", "!");
        value = value.replace("%3F", "?");
        value = value.replace("%2C", ",");
        return value;
    }

    private String getSafeValue(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    private HttpResponse createResponse(int statusCode, String reasonPhrase, String body) {
        HttpResponse response = new HttpResponse("HTTP/1.1", statusCode, reasonPhrase, body);
        response.addHeader("Content-Type", "text/plain");
        return response;
    }
}