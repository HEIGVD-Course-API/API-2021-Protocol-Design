package ch.heigvd.api.calc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator server implementation - multi-thread
 */
public class Server {

    private final static Logger LOG = Logger.getLogger(Server.class.getName());
    private final static int PORT = 6996;


    /**
     * Main function to start the server
     */
    public static void main(String[] args) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        (new Server()).start();
    }

    /**
     * Start the server on a listening socket.
     */
    private void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();

                ServerWorker worker = new ServerWorker(clientSocket);
                Thread thread = new Thread(worker);
                thread.start();

            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error while creating server socket or accepting socket.", e);
        }
    }
}
