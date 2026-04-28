import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppLogger {

    // Path to the log file where all server activity is stored.
    private static final String LOG_FILE = "logs/server.log";

    // Shared timestamp format used for every log entry.
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Central logging method used by all other helper methods.
    // It creates a timestamped log entry, prints it to the console,
    // and appends it to the log file.
    private static void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logEntry = "[" + timestamp + "] [" + level + "] " + message;

        // Print the log entry to the console for immediate visibility.
        System.out.println(logEntry);

        // Append the same log entry to the log file.
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE, true)))) {
            writer.println(logEntry);
        } catch (IOException e) {
            // If file logging fails, print the error to standard error.
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }

    // Logs a general informational message.
    public static void info(String message) {
        log("INFO", message);
    }

    // Logs a warning message for non-critical issues.
    public static void warning(String message) {
        log("WARNING", message);
    }

    // Logs an error message for failures or unexpected problems.
    public static void error(String message) {
        log("ERROR", message);
    }

    // Logs an error message and includes exception details.
    public static void error(String message, Exception e) {
        log("ERROR", message + " | Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
    }

    // Logs when the server starts and records the port number.
    public static void logServerStart(int port) {
        info("Server started on port " + port);
    }

    // Logs when the server shuts down.
    public static void logServerStop() {
        info("Server stopped");
    }

    // Logs when a client successfully connects to the server.
    public static void logClientConnected(String clientAddress) {
        info("Client connected: " + clientAddress);
    }

    // Logs the HTTP method and path for an incoming request.
    public static void logRequest(String method, String path) {
        info("Request received: method=" + method + ", path=" + path);
    }

    // Logs the path of a file that was successfully served.
    public static void logFileServed(String filePath) {
        info("File served: " + filePath);
    }

    // Logs a 404 event when a requested path cannot be found.
    public static void logNotFound(String path) {
        warning("404 Not Found: " + path);
    }

    // Logs details about a malformed or invalid request.
    public static void logMalformedRequest(String details) {
        warning("Malformed request: " + details);
    }

    // Logs when a path is rejected for being unsafe.
    public static void logUnsafePathRejected(String path) {
        warning("Rejected unsafe path: " + path);
    }

    // Logs why form validation failed.
    public static void logFormValidationFailure(String reason) {
        warning("Form validation failure: " + reason);
    }

    // Logs when a form submission is successfully stored.
    public static void logSubmissionStored(String details) {
        info("Submission stored: " + details);
    }

    // Logs internal server errors together with exception details.
    public static void logInternalError(String details, Exception e) {
        error("Internal error: " + details, e);
    }
}