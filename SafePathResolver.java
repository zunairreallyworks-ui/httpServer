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
            return null;
        }

        String cleanedPath = requestedPath.trim();

        if (!cleanedPath.startsWith("/")) {
            return null;
        }

        if (cleanedPath.equals("/")) {
            cleanedPath = "index.html";
        } else {
            cleanedPath = cleanedPath.substring(1);
        }

        Path resolvedPath = base.resolve(cleanedPath).normalize();

        if (!resolvedPath.startsWith(base)) {
            return null;
        }

        if (!Files.exists(resolvedPath)) {
            return null;
        }

        if (!Files.isRegularFile(resolvedPath)) {
            return null;
        }

        return resolvedPath;
    }
}