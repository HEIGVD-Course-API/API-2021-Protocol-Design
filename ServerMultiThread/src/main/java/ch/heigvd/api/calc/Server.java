package ch.heigvd.api.calc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Calculator server implementation - multi-thread
 */
public class Server {

    private final static Logger LOG = Logger.getLogger(Server.class.getName());

    /**
     * Main function to start the server
     */
    public static void main(String[] args) throws IOException {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        (new Server()).start();
    }

    /**
     * Start the server on a listening socket.
     */
    private void start() throws IOException {
        ServerWorker.OPERATIONS.add(new AddOperation());
        ServerWorker.OPERATIONS.add(new SupOperation());
        ServerWorker.OPERATIONS.add(new MulOperation());

        ServerSocket server = new ServerSocket(1997);
        while (true) {
            Socket s = server.accept();
            new ServerWorker(s).run();
        }
    }
}
