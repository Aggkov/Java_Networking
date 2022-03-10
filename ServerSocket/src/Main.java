import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Open ServerSocket
        try (ServerSocket server = new ServerSocket(1234)) {

            // Accept a Client
            Socket sockClient = server.accept();

            Scanner readFromClient = new Scanner(sockClient.getInputStream(),
                    StandardCharsets.UTF_8);
            PrintWriter writeToClient = new PrintWriter(sockClient.getOutputStream(),
                    true, StandardCharsets.UTF_8);

            // do something with your client

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}