import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer {

    // Starts the server, listens for incoming client connections,
    // and passes each connection to a worker thread from the pool.
    public void server() {
        // Fixed-size thread pool used to handle multiple client connections concurrently.
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            AppLogger.logServerStart(8080);
            System.out.println("Server running on port 8080...");

            // Keep accepting client connections for as long as the server is running.
            while (true) {
                Socket socket = serverSocket.accept();

                // Create a handler for the connected client.
                ConnectionHandler connectionHandler = new ConnectionHandler(socket);

                // Submit the handler to the thread pool so it can run on a worker thread.
                threadPool.submit(connectionHandler);
            }
        } catch (IOException e) {
            // Log any server-level I/O failures.
            AppLogger.logInternalError("Server failed in MainServer", e);
            e.printStackTrace();
        } finally {
            // Log when the server stops.
            AppLogger.logServerStop();
        }
    }

    // Program entry point. Creates the server object and starts it.
    public static void main(String[] args) {
        MainServer mainServer = new MainServer();
        mainServer.server();
    }
}