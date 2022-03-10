import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try (Socket client = new Socket("127.0.0.1", 1234)) {

            Scanner readFromInput = new Scanner(System.in);
            Scanner readFromServer = new Scanner(client.getInputStream(),
                    StandardCharsets.UTF_8);
            PrintWriter writeToServer = new PrintWriter(client.getOutputStream(),
                    true, StandardCharsets.UTF_8);

            System.out.println("Enter Username: ");
            String username = readFromInput.nextLine();
            writeToServer.println(username);
            while(true) {
                System.out.print("> ");
                String request = readFromInput.nextLine();
                writeToServer.println(request);
                if (request.equals("quit")) {
                    System.out.println("Bye Bye");
                    break;
                }
                String response = readFromServer.nextLine();
                System.out.println("Server response: " + response);;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}