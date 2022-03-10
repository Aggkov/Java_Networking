import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {

        // make the server
        try (AsynchronousServerSocketChannel server =
                     AsynchronousServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(1234));
            System.out.println("Server ready!");

            // Will wait for incoming connection from Client
            Future<AsynchronousSocketChannel> futureClient = server.accept();
            // Connection established
            AsynchronousSocketChannel sockClient = futureClient.get();

            // Middleman between Server-Socket and Client-Socket.
            // Here we have 1 buffer for
            // reading and writing data.
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            // read
            Future<Integer> futResponse = sockClient.read(buffer);
            int bytes = futResponse.get();
            buffer.flip();
            String response = StandardCharsets.UTF_8.decode(buffer).toString();
            System.out.println("Read " + bytes + " bytes from client (msg: " + response + ")");
            // write
            buffer.clear();
            buffer.put(StandardCharsets.UTF_8.encode("Hey Client!!!"));
            buffer.flip();
            futResponse = sockClient.write(buffer);
            bytes = futResponse.get();
            System.out.println("Sent " + bytes + " bytes to client");

        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}