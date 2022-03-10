import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private ServerSocket server;
    private Set<Handler> users = Collections.synchronizedSet(new HashSet<>());

    Server(int port) {
        // make the server
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        ExecutorService es = Executors.newFixedThreadPool(1000);
        System.out.println("Server running (Ctrl+C) to quit!");

        // wait for incoming connections
        while (true) {
            try {
                Socket sockClient = server.accept();
                // Handler Thread will take care of communication between Clients
                // 1 Thread per Client
                Handler handler = new Handler(sockClient, users);
                // In order to broadcast to all Clients the message we need access to a data structure
                // with every Thread
                users.add(handler);

                // assign the socket to a thread
                es.execute(handler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}