package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator client implementation
 */
public class Client {

    private static final Logger LOG = Logger.getLogger(Client.class.getName());
    private static final int PORT = 6996;
    private static final String HOST = "127.0.0.1";

    /**
     * Main function to run client
     *
     * @param args no args required
     */
    public static void main(String[] args) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        Socket clientSocket = null;
        BufferedWriter out = null;
        BufferedReader in = null;
        BufferedReader stdin;

        try {
            // Connect to the server
            clientSocket = new Socket(HOST, PORT);

            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            stdin = new BufferedReader(new InputStreamReader(System.in));

            // Initialize the dialog with the server
            while (!clientSocket.isClosed()) {
                String line;

                while ((line = in.readLine()) != null) {
                    System.out.println(line);

                    if (line.startsWith("RESULT") || line.isEmpty()) {
                        break;
                    }
                }

                String input = stdin.readLine();

                // Send the command to the server
                out.write(input + "\n");
                out.flush();
                LOG.log(Level.INFO, "Sent: " + input);

                if (input.equals("EXIT")) {
                    return;
                }
            }

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.toString(), ex);
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.toString(), ex);
            }
            try {
                if (in != null) in.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.toString(), ex);
            }
            try {
                if (clientSocket != null) clientSocket.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.toString(), ex);
            }
        }
    }
}
