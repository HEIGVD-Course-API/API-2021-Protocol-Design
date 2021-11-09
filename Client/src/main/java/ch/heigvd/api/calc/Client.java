package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator client implementation
 */
public class Client {

    private static final Logger LOG = Logger.getLogger(Client.class.getName());

    private static final int port = 11111;
    private static Socket socket;
    private static BufferedReader in = null;
    private static BufferedWriter out = null;

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
        try {
            socket = new Socket("localhost", port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            System.out.println("Salut je suis le client");
            String q = "";
            String r = "";
            while (true) {
                q = stdin.readLine();
                System.out.printf("Je vais envoyer: '%s'\n", q);
                out.write(q + "\r\n");
                out.flush();

                r = in.readLine();
                System.out.println(r);
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }
}
