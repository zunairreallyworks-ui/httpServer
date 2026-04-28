import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SafePathResolver {

    // Base directory that all requested files must stay within.
    private Path base;

    // Raw path requested by the client.
    private String requestedPath;

    // Set the fixed public directory and store the requested path.
    public SafePathResolver(String requestedPath) {
        this.base = Paths.get("public").toAbsolutePath().normalize();
        this.requestedPath = requestedPath;
    }

    // Resolves and validates the requested path before it is used for file access.
    public Path safePath() {
        // Reject null or blank paths.
        if (requestedPath == null || requestedPath.trim().isEmpty()) {
            return null;
        }

        String cleanedPath = requestedPath.trim();

        // Only allow paths that begin with a forward slash.
        if (!cleanedPath.startsWith("/")) {
            return null;
        }

        // Map the root request to the default homepage file.
        if (cleanedPath.equals("/")) {
            cleanedPath = "index.html";
        } else {
            // Remove the leading slash so the path can be safely resolved against the base directory.
            cleanedPath = cleanedPath.substring(1);
        }

        // Resolve the requested path against the public directory and normalise it
        // to remove sequences such as ../
        Path resolvedPath = base.resolve(cleanedPath).normalize();

        // Reject any path that escapes outside the intended public directory.
        if (!resolvedPath.startsWith(base)) {
            AppLogger.logUnsafePathRejected(requestedPath);
            return null;
        }

        // Reject paths that do not point to an existing file.
        if (!Files.exists(resolvedPath)) {
            return null;
        }

        // Reject anything that is not a regular file, such as a directory.
        if (!Files.isRegularFile(resolvedPath)) {
            return null;
        }

        return resolvedPath;
    }
}