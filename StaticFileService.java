import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class StaticFileService {

    public StaticFileService() {
    }

    public HttpResponse handleGet(String path) {
        SafePathResolver safePathResolver = new SafePathResolver(path);
        Path safePath = safePathResolver.safePath();

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

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(safePath.toFile()))) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }

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