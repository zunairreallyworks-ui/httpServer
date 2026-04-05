import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
public class ConnectionHandler {
    private Socket socket;

    public ConnectionHandler(Socket socket){
        this.socket = socket;
    }
    public void handle(){
        
        System.out.println("Connected: " + socket);
        try(
        BufferedReader reader = new BufferedReader(
        new InputStreamReader(socket.getInputStream())
        );
        BufferedWriter writer = new BufferedWriter(
            new OutputStreamWriter(socket.getOutputStream())
        )){
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
        return;
        }
        RequestParser parser = new RequestParser(requestLine);
        HttpRequest request = parser.parse();
        Router router = new Router();
        HttpResponse route = router.route(request);
        writer.write(route.buildResponse());
        writer.flush();
        }
        catch(IOException e){e.printStackTrace();}
        finally{
            try {
                socket.close();
                
            } catch (IOException e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
        
    }
}
