import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Map;

public class SubmissionStore {

    // Shared lock used to prevent multiple threads writing to the file at the same time.
    private static final Object FILE_LOCK = new Object();

    // Absolute path to the file where form submissions are stored.
    private final Path storageFile;

    // Set the storage file location when the store is created.
    public SubmissionStore() {
        this.storageFile = Paths.get("data", "submissions.txt").toAbsolutePath().normalize();
    }

    // Saves a validated form submission to the storage file.
    public boolean saveSubmission(Map<String, String> submissionData) {
        // Reject null or empty submission data.
        if (submissionData == null || submissionData.isEmpty()) {
            return false;
        }

        // Lock file writing so only one thread can write at a time.
        synchronized (FILE_LOCK) {
            try {
                Path parent = storageFile.getParent();

                // Create the parent directory if it does not already exist.
                if (parent != null) {
                    Files.createDirectories(parent);
                }

                // Build a single text record from the submission data.
                String record = buildRecord(submissionData);

                // Open the file in append mode and create it if it does not exist.
                try (BufferedWriter writer = Files.newBufferedWriter(
                    storageFile,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
                )) {
                    writer.write(record);
                    writer.newLine();
                }

                // Log the stored submission after it has been written successfully.
                AppLogger.logSubmissionStored(record);
                return true;

            } catch (IOException e) {
                // Log any storage failure and report that saving did not succeed.
                AppLogger.logInternalError("Failed to store submission", e);
                e.printStackTrace();
                return false;
            }
        }
    }

    // Builds a single line record containing a timestamp and all submitted fields.
    private String buildRecord(Map<String, String> submissionData) {
        StringBuilder record = new StringBuilder();

        record.append("timestamp=").append(LocalDateTime.now());

        for (Map.Entry<String, String> entry : submissionData.entrySet()) {
            record.append(" | ")
                  .append(entry.getKey())
                  .append("=")
                  .append(entry.getValue());
        }

        return record.toString();
    }
}