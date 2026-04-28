import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class StaticFileService {

    // Default constructor for the static file service.
    public StaticFileService() {
    }

    // Handles GET requests for files stored inside the public directory.
    public HttpResponse handleGet(String path) {
        // Resolve the requested path safely before attempting any file access.
        SafePathResolver safePathResolver = new SafePathResolver(path);
        Path safePath = safePathResolver.safePath();

        // If the resolved path is invalid, unsafe, or does not point to a valid file,
        // return a 404 response.
        if (safePath == null) {
            AppLogger.logNotFound(path);

            HttpResponse response = new HttpResponse(
                "HTTP/1.1",
                404,
                "Not Found",
                "File not found"
            );
            response.addHeader("Content-Type", "text/plain; charset=UTF-8");
            return response;
        }

        StringBuilder fileContent = new StringBuilder();

        // Read the requested file line by line and build the response body.
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(safePath.toFile()))) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }

            // Log successful file serving.
            AppLogger.logFileServed(safePath.toString());

            HttpResponse response = new HttpResponse(
                "HTTP/1.1",
                200,
                "OK",
                fileContent.toString()
            );
            response.addHeader("Content-Type", "text/html; charset=UTF-8");
            return response;

        } catch (IOException e) {
            // Return a controlled 500 response if the file cannot be read.
            AppLogger.logInternalError("Failed to read file: " + safePath, e);

            HttpResponse response = new HttpResponse(
                "HTTP/1.1",
                500,
                "Internal Server Error",
                "Error reading file"
            );
            response.addHeader("Content-Type", "text/plain; charset=UTF-8");
            return response;
        }
    }
}