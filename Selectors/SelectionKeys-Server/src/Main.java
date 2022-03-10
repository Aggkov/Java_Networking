import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {


        // make the selector and the server
        try (Selector selector = Selector.open();
             ServerSocketChannel server = ServerSocketChannel.open()) {

            server.configureBlocking(false); // important! Selectors only use non-blocking channels
            server.bind(new InetSocketAddress(1234));
            server.register(selector, SelectionKey.OP_ACCEPT);

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (true) {
                Thread.sleep(1000);
                selector.selectNow();
                // Set of events
                Set<SelectionKey> keyEvents = selector.selectedKeys();
                // Iterator over Set
                Iterator<SelectionKey> keyIterator = keyEvents.iterator();
                System.out.print("\nEvents: ");
                while (keyIterator.hasNext()) {
                    SelectionKey keyEvent = keyIterator.next();
                    if (keyEvent.isAcceptable()) {
                        System.out.println("Incoming connection");
                        SocketChannel client = server.accept();
                        // Register as READ
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                    }
                    else if (keyEvent.isReadable()) {
                        System.out.println("Reading Event");
                        SocketChannel client = (SocketChannel) keyEvent.channel();

                        //read message
                        buffer.clear();
                        int bytes = client.read(buffer);
                        buffer.flip();
                        String response = StandardCharsets.UTF_8.decode(buffer).toString();
                        System.out.println("Read " + bytes + " bytes from client (msg: " + response + ")");
                        if (response.equals("quit"))
                            client.close();
                        else
                            client.register(selector, SelectionKey.OP_WRITE);
                    }
                    else if (keyEvent.isWritable()) {
                        System.out.println("Writing Event");
                        SocketChannel client = (SocketChannel) keyEvent.channel();

                        buffer.clear();
                        buffer.put(StandardCharsets.UTF_8.encode("Hey Client!"));
                        buffer.flip();
                        int bytes = client.write(buffer);
                        System.out.println("Sent " + bytes + " bytes to client");
                        client.register(selector, SelectionKey.OP_READ);
                    }

                    keyIterator.remove();
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}