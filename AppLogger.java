import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppLogger {

    private static final String LOG_FILE = "logs/server.log";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logEntry = "[" + timestamp + "] [" + level + "] " + message;

        System.out.println(logEntry);

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE, true)))) {
            writer.println(logEntry);
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }

    public static void info(String message) {
        log("INFO", message);
    }

    public static void warning(String message) {
        log("WARNING", message);
    }

    public static void error(String message) {
        log("ERROR", message);
    }

    public static void error(String message, Exception e) {
        log("ERROR", message + " | Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
    }

    public static void logServerStart(int port) {
        info("Server started on port " + port);
    }

    public static void logServerStop() {
        info("Server stopped");
    }

    public static void logClientConnected(String clientAddress) {
        info("Client connected: " + clientAddress);
    }

    public static void logRequest(String method, String path) {
        info("Request received: method=" + method + ", path=" + path);
    }

    public static void logFileServed(String filePath) {
        info("File served: " + filePath);
    }

    public static void logNotFound(String path) {
        warning("404 Not Found: " + path);
    }

    public static void logMalformedRequest(String details) {
        warning("Malformed request: " + details);
    }

    public static void logUnsafePathRejected(String path) {
        warning("Rejected unsafe path: " + path);
    }

    public static void logFormValidationFailure(String reason) {
        warning("Form validation failure: " + reason);
    }

    public static void logSubmissionStored(String details) {
        info("Submission stored: " + details);
    }

    public static void logInternalError(String details, Exception e) {
        error("Internal error: " + details, e);
    }
}