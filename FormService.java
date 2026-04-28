import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class FormService {

    // Handles storing validated form submissions.
    private final SubmissionStore submissionStore;

    // Create a FormService and initialise the submission storage dependency.
    public FormService() {
        this.submissionStore = new SubmissionStore();
    }

    // Handles form submissions sent in the request body, typically from POST requests.
    public HttpResponse handlePost(HttpRequest request) {
        if (request == null) {
            return createResponse(400, "Bad Request", "Request was null");
        }

        String body = request.getBody();

        // Reject the request if no form body was provided.
        if (body == null || body.trim().isEmpty()) {
            return createResponse(400, "Bad Request", "Form body is empty");
        }

        // Parse the raw form body into key-value pairs and pass it for validation.
        Map<String, String> formData = parseFormData(body);
        return handleFormData(formData);
    }

    // Handles form submissions sent through query parameters, typically from GET requests.
    public HttpResponse handleGet(HttpRequest request) {
        if (request == null) {
            return createResponse(400, "Bad Request", "Request was null");
        }

        Map<String, String> formData = request.getParameters();

        // Reject the request if no parameters were supplied.
        if (formData == null || formData.isEmpty()) {
            return createResponse(400, "Bad Request", "Form data is empty");
        }

        return handleFormData(formData);
    }

    // Shared validation and submission logic used by both GET and POST form handling.
    private HttpResponse handleFormData(Map<String, String> formData) {
        if (formData == null || formData.isEmpty()) {
            return createResponse(400, "Bad Request", "Form data is empty");
        }

        // Read and safely clean the expected form fields.
        String name = getSafeValue(formData.get("name"));
        String email = getSafeValue(formData.get("email"));
        String message = getSafeValue(formData.get("message"));

        // All fields must be present and non-empty.
        if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
            return createResponse(400, "Bad Request", "All fields are required");
        }

        // Create a new map containing the cleaned submission data.
        Map<String, String> submissionData = new HashMap<>();
        submissionData.put("name", name);
        submissionData.put("email", email);
        submissionData.put("message", message);

        // Attempt to save the validated submission.
        boolean saved = submissionStore.saveSubmission(submissionData);

        if (!saved) {
            return createResponse(500, "Internal Server Error", "Could not save submission");
        }

        return createResponse(200, "OK", "Form submitted successfully");
    }

    // Converts a URL-encoded form body into a map of keys and values.
    private Map<String, String> parseFormData(String body) {
        Map<String, String> formData = new HashMap<>();

        // Split the form body into individual key-value pairs.
        String[] pairs = body.split("&");

        for (String pair : pairs) {
            if (pair == null || pair.trim().isEmpty()) {
                continue;
            }

            // Split each pair into key and value, limiting to two parts
            // so values containing '=' are not broken incorrectly.
            String[] keyValue = pair.split("=", 2);

            String key = decodeFormValue(keyValue[0]);
            String value = keyValue.length > 1 ? decodeFormValue(keyValue[1]) : "";

            // Ignore empty keys and only store valid entries.
            if (!key.isEmpty()) {
                formData.put(key, value);
            }
        }

        return formData;
    }

    // Decodes URL-encoded form values such as spaces and special characters.
    private String decodeFormValue(String value) {
        if (value == null) {
            return "";
        }

        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // UTF-8 should always be supported, so reaching this point
            // indicates an unexpected decoding problem.
            throw new IllegalArgumentException("Invalid form encoding");
        }
    }

    // Returns a trimmed value, or an empty string if the input is null.
    private String getSafeValue(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }

    // Creates a plain text HTTP response with the given status and body.
    private HttpResponse createResponse(int statusCode, String reasonPhrase, String body) {
        HttpResponse response = new HttpResponse("HTTP/1.1", statusCode, reasonPhrase, body);
        response.addHeader("Content-Type", "text/plain; charset=UTF-8");
        return response;
    }
}