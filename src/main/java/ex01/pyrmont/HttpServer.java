package ex01.pyrmont;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

    private boolean shutdown = false;

    public static void main(String[] args) {
        HttpServer server = new HttpServer();

        server.await();
    }

    private void await() {
        ServerSocket serverSocket = null;
        System.out.println(WEB_ROOT);
        int port = 8080;
        try{

            serverSocket = new ServerSocket(port,5, InetAddress.getByName("127.0.0.1"));
        }catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }

        while(!shutdown){
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;

            try{
                socket = serverSocket.accept();
                input = socket.getInputStream();
                output = socket.getOutputStream();

                Request reqest = new Request(input);
                reqest.parse();

                Response response = new Response(output);
                response.setRequest(reqest);
                response.sendStaticResource();
                socket.close();
                shutdown = reqest.getUri().equals(SHUTDOWN_COMMAND);
            }catch (Exception e){
                e.printStackTrace();
                continue;
            }

        }
    }
}
