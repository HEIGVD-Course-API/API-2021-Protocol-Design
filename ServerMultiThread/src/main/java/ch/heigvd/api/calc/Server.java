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

        /* TODO: implement the receptionist server here.
         *  The receptionist just creates a server socket and accepts new client connections.
         *  For a new client connection, the actual work is done in a new thread
         *  by a new ServerWorker.
         */
        System.out.println("Starting server...");

        ServerSocket serverSocket = null;

        int portNumber = 2048;

        try {
            // creates the socket and wait for the client connection
            serverSocket = new ServerSocket(portNumber);


            // pass the socket to the handler
            // launch the thread
            //handleClient(clientSocket);
            while(true) {
                System.out.println("Waiting for  client connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection. Launching thread...");
                ServerWorker worker = new ServerWorker(clientSocket);
                System.out.println("Init ok...");
                new Thread(new ServerWorker(clientSocket)).start();
                System.out.println("Thread ok...");
                System.out.println("Thread is running...");
            }

        }catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        } finally {
            LOG.log(Level.INFO, "We are done. Cleaning up resources, closing streams and sockets...");
            try {
                serverSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null,
                        ex);
            }
        }

    }
}
