package ch.heigvd.api.calc;

import sun.misc.Signal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.spec.RSAOtherPrimeInfo;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator server implementation - multi-thread
 */
public class Server {
    private final static int port = 9999;
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
        System.out.println("Valeur max : " + Double.POSITIVE_INFINITY);
        // Créer le socket listen
        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Impossible d'ouvrir le serveur sur le port " + port, e);
            return;
        }

        // TODO : Proper way to end server ?
        // TODO : Proper way to kill all threads (with clean client terminaison) =
        while(true) {
            LOG.info("Waiting for client on port " + port);
            try {
                Socket clientSocket = serverSocket.accept();
                LOG.info("A new client has arrived. Starting a new thread and delegating work to a new servant...");
                new Thread(new ServerWorker(clientSocket)).start();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Connexion reçue, impossible de créer le ServerWorker", ex);
            }
        }
    }
}
