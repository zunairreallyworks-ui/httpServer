import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer{
    public void server(){
        try(ServerSocket serverSocket = new ServerSocket(8080)){
            while(true){
            Socket socket = serverSocket.accept();
            ConnectionHandler connectionHandler = new ConnectionHandler(socket);
            connectionHandler.handle();
            }
        }
        catch (IOException e){
        e.printStackTrace();
        }
    }
}