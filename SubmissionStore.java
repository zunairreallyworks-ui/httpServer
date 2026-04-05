import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Map;

public class SubmissionStore {

    private final Path storageFile;

    public SubmissionStore() {
        this.storageFile = Paths.get("data", "submissions.txt").toAbsolutePath().normalize();
    }

    public boolean saveSubmission(Map<String, String> submissionData) {
        if (submissionData == null || submissionData.isEmpty()) {
            return false;
        }

        try {
            Path parent = storageFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            String record = buildRecord(submissionData);

            try (BufferedWriter writer = Files.newBufferedWriter(
                    storageFile,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            )) {
                writer.write(record);
                writer.newLine();
            }

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

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