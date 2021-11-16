package ch.heigvd.api.calc;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator server implementation - multi-thread
 */
public class Server {
    final int LISTENING_PORT = 9999;
    private final static Logger LOG = Logger.getLogger(Server.class.getName());

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
        ServerSocket receptionistSocket = null;
        //Socket clientSocket = null;
        try {
            LOG.log(Level.INFO, "Waiting (blocking) for a new client");

            receptionistSocket = new ServerSocket(LISTENING_PORT);
            while(true) {
                Socket clientSocket = receptionistSocket.accept();
                new Thread(new ServerWorker(clientSocket)).start();
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
}
