import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer {

    public void server() {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            AppLogger.logServerStart(8080);
            System.out.println("Server running on port 8080...");

            while (true) {
                Socket socket = serverSocket.accept();
                ConnectionHandler connectionHandler = new ConnectionHandler(socket);
                threadPool.submit(connectionHandler);
            }
        } catch (IOException e) {
            AppLogger.logInternalError("Server failed in MainServer", e);
            e.printStackTrace();
        } finally {
            AppLogger.logServerStop();
        }
    }

    public static void main(String[] args) {
        MainServer mainServer = new MainServer();
        mainServer.server();
    }
}