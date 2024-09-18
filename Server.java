import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private int portNo;

    public Server(int portNo){
        this.portNo=portNo;
    }

    private void processConnection() throws IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
        OutputStream outStream = clientSocket.getOutputStream();

        //*** Application Protocol *****
        String request = in.readLine();
        String[] tokens = request.split(" ");
        String fileName = tokens[1];

        if(fileName.equals("/")){
            fileName = "/home.html";
        }
        File file = new File("docroot" + fileName);

        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: " + getContentType(file.getName()));
        out.println("Content-Length: " + file.length());
        out.println();

        FileInputStream inStream = new FileInputStream(file);
        int data;
        while((data = inStream.read()) != -1){
            outStream.write(data);
        }
       
        in.close();
        out.close();

    }

    public String getContentType(String file){
        if(file.endsWith(".html")){
            return "text/html";
        }
        if(file.endsWith(".css")){
            return "text/css";
        }
        if(file.endsWith(".png")){
            return "image/png";
        }
        return null;
    }
    

    public void run() throws IOException{
        boolean running = true;
       
        serverSocket = new ServerSocket(portNo);
        System.out.printf("Listen on Port: %d\n",portNo);
        while(running){
            clientSocket = serverSocket.accept();
            //** Application Protocol
            processConnection();
            clientSocket.close();
        }
        serverSocket.close();
    }
    public static void main(String[] args0) throws IOException{
        Server server = new Server(8080);
        server.run();
    }
}
