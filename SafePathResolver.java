import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SafePathResolver {

    private Path base;
    private String requestedPath;

    public SafePathResolver(String requestedPath) {
        this.base = Paths.get("public").toAbsolutePath().normalize();
        this.requestedPath = requestedPath;
    }

    public Path safePath() {
        if (requestedPath == null || requestedPath.trim().isEmpty()) {
            System.out.println("Error: path is missing");
            return null;
        }

        String cleanedPath = requestedPath;

        if (cleanedPath.startsWith("/") && cleanedPath.length() > 1) {
            cleanedPath = cleanedPath.substring(1);
        } else {
            System.out.println("Error: invalid path format");
            return null;
        }

        Path resolvedPath = base.resolve(cleanedPath).normalize();

        if (!resolvedPath.startsWith(base)) {
            System.out.println("Rejected traversal attack");
            return null;
        }

        if (!Files.exists(resolvedPath)) {
            System.out.println("File does not exist");
            return null;
        }

        if (!Files.isRegularFile(resolvedPath)) {
            System.out.println("Irregular file");
            return null;
        }

        System.out.println("Valid and found");
        return resolvedPath;
    }
}