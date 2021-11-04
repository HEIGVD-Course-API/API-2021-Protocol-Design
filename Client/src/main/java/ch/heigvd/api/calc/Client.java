package ch.heigvd.api.calc;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator client implementation
 */
public class Client {

    private static final Logger LOG = Logger.getLogger(Client.class.getName());

    private static Socket clientSocket = null;
    private static BufferedReader reader = null;
    private static BufferedWriter writer = null;

    /**
     * Main function to run client
     *
     * @param args no args required
     */
    public static void main(String[] args) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        BufferedReader stdin = null;

        /* TODO: Implement the client here, according to your specification
         *   The client has to do the following:
         *   - connect to the server
         *   - initialize the dialog with the server according to your specification
         *   - In a loop:
         *     - read the command from the user on stdin (already created)
         *     - send the command to the server
         *     - read the response line from the server (using BufferedReader.readLine)
         */

        stdin = new BufferedReader(new InputStreamReader(System.in));

    }

    private static boolean connectToServer(String host, int listenPort) {
        try {
            clientSocket = new Socket(host, listenPort);
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Can't open connection", ex);
            return false;
        }
        return true;
    }

    private static boolean closeConnection() {
        return true;
    }
}
