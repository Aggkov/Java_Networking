import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {

        // make the client
        try (AsynchronousSocketChannel client =
                     AsynchronousSocketChannel.open()) {
            Thread.sleep(1000);
            Future<Void> checkConn = client.connect(new InetSocketAddress("127.0.0.1", 1234));
            checkConn.get();

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            // write with Future <> (buffer is the middleman)
            buffer.put(StandardCharsets.UTF_8.encode("Hey Server!"));
            buffer.flip();
            Future<Integer> futResponse = client.write(buffer);
            int bytes = futResponse.get();
            System.out.println("Sent " + bytes + " bytes to server");

            // read with Future <> (buffer is the middleman)
            buffer.clear();
            futResponse = client.read(buffer);
            bytes = futResponse.get();
            buffer.flip();
            String response = StandardCharsets.UTF_8.decode(buffer).toString();
            System.out.println("Read " + bytes + " bytes from client (msg: " + response + ")");

        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}