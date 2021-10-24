package ch.heigvd.api.calc;

import sun.misc.Signal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.spec.RSAOtherPrimeInfo;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator server implementation - multi-thread
 */
public class Server {
    private final static int port = 9999;
    private final static Logger LOG = Logger.getLogger(Server.class.getName());
    private static ArrayList<ServerWorker> workers = new ArrayList<>();
    private static ArrayList<Thread> threads = new ArrayList<>();

    /**
     * Main function to start the server
     */
    public static void main(String[] args) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        (new Server()).start();
    }

    private static void end() {
        LOG.info("SERVER - Stopping server");

        for (ServerWorker worker : workers) {
            worker.terminaison("Server");
        }

        for (Thread thread : threads) {
            thread.interrupt();
        }

        System.exit(0);
    }

    /**
     * Start the server on a listening socket.
     */
    private void start() {
        // Créer le socket listen
        ServerSocket serverSocket;
        ServerWorker worker;
        Thread thread;

        Signal.handle(new Signal("INT"), signal -> end());

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "SERVER - Can't open server on port " + port, e);
            return;
        }

        // TODO : Proper way to end server ?
        // TODO : Proper way to kill all threads (with clean client terminaison) =
        LOG.info("SERVER - Waiting for clients on port " + port);
        while(true) {
            try {
                Socket clientSocket = serverSocket.accept();
                LOG.info("SERVER - New client");

                // Creating thread, starting and saving it
                worker = new ServerWorker(clientSocket);
                thread = new Thread(worker);
                threads.add(thread);
                workers.add(worker);
                thread.start();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "SERVER - Can't accept new client !", e);
            }
        }
    }
}
