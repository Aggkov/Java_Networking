import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {

        try (Socket client = new Socket("127.0.0.1", 1234)) {
            ExecutorService es = Executors.newFixedThreadPool(2);

            // Streams
            Scanner readFromInput = new Scanner(System.in);
            Scanner readFromServer = new Scanner(client.getInputStream(),
                    StandardCharsets.UTF_8);
            PrintWriter writeToServer = new PrintWriter(client.getOutputStream(),
                    true, StandardCharsets.UTF_8);

            // 1st thread: Read from input and send to server
            System.out.println("Enter Username: ");
            String username = readFromInput.nextLine();
            writeToServer.println(username);

            while(true) {
                System.out.print("> ");
                String request = readFromInput.nextLine();
                writeToServer.println(request);
                if (request.equals("quit")) {
                    es.shutdownNow();
                    System.out.println("Bye Bye");
                    break;
                }
            }

            // 2nd thread: read from server and print to the screen
            es.execute(()-> {
                while(true) {
                    try {
                        String response = readFromServer.nextLine();
                        if (response.equals("quit")) {
                            es.shutdownNow();
                            return;
                        }
                        System.out.print(response + "\n> ");
                    } catch (NoSuchElementException e) {
                        return;
                    }
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}