import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {

        // make the server and a thread pool
        try (ServerSocket server = new ServerSocket(1234)) {
            ExecutorService es = Executors.newFixedThreadPool(1000);
            System.out.println("Server ready!");

            while (true) {
                // wait for incoming connections
                Socket sockClient = server.accept();

                // assign the socket to a thread
                es.execute(() -> {
                    try {
                        // make streams
                        Scanner readFromClient = new Scanner(sockClient.getInputStream(),
                                StandardCharsets.UTF_8);
                        PrintWriter writeToClient = new PrintWriter(sockClient.getOutputStream(),
                                true, StandardCharsets.UTF_8);

                        // make some login
                        String username = readFromClient.nextLine();
                        System.out.println("User '" + username + "' is in.");

                        // interact with your client
                        while (true) {
                            String request = readFromClient.nextLine();
                            System.out.println("read from " + username + ": " + request);
                            if (request.equals("quit")) {
                                System.out.println("Client is done!");
                                break;
                            }
                            String response = "<server echoing: " + request + ">";
                            writeToClient.println(response);
                        }
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                });
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}