package ch.heigvd.api.calc;

import sun.misc.Signal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator server implementation - multi-thread
 */
public class Server {
    static private final int port = 9999;
    static private final Logger LOG = Logger.getLogger(Server.class.getName());
    static private ArrayList<ServerWorker> workers = new ArrayList<>();
    static private ArrayList<Thread> threads = new ArrayList<>();
    static private ServerSocket serverSocket;
    static private boolean running = true;

    /**
     * Main function to start the server
     */
    static public void main(String[] args) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        (new Server()).start();
    }

    /**
     * Clean stop of server
     * Tries to call the end of all threads
     * Then tries to interrupt threads
     */
    static private void end() throws InterruptedException {
        LOG.info("SERVER - Stopping server");

        // Interrupt threads
        for (Thread thread : threads) {
            thread.interrupt();
        }

        // Calls all workers' end function to clean ressources
        for (ServerWorker worker : workers) {
            worker.end_client("Server");
        }

        // Wait for end of threads
        for (Thread thread : threads) {
            thread.join();
        }

        // Cleaning master server ressources
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Can't close serverSocket", e);
        }

        running = false;

        // Closes server
        System.exit(0);
    }

    /**
     * Start the server on a listening socket.
     */
    private void start() {
        // Catch CTRL+C for "clean" ending
        Signal.handle(new Signal("INT"), signal -> {
            try {
                end();
            } catch (InterruptedException e) {
                LOG.log(Level.SEVERE, "Can't end threads cleanly", e);
                e.printStackTrace();
            }
        });

        // Try to open server as passive listener
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "SERVER - Can't open server on port " + port, e);
            return;
        }

        // Wait for new client then send it to separated thread
        LOG.info("SERVER - Waiting for clients on port " + port);
        while(running) {
            try {
                // Waiting for new client
                Socket clientSocket = serverSocket.accept();
                LOG.info("SERVER - New client");

                // Creating, starting and saving thread for new client
                ServerWorker worker = new ServerWorker(clientSocket);
                Thread thread = new Thread(worker);
                threads.add(thread);
                workers.add(worker);
                thread.start();
            } catch (IOException e) {
                if (running)
                    LOG.log(Level.SEVERE, "SERVER - Can't accept new client !", e);
                else
                    LOG.info("Server stopped.");
            }
        }
    }
}
