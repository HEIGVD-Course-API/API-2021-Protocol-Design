package ch.heigvd.api.calc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.*;
import java.net.Socket;

/**
 * Calculator client implementation
 */
public class Client {

    private static final Logger LOG = Logger.getLogger(Client.class.getName());

    /**
     * Main function to run client
     *
     * @param args no args required
     */
    public static void main(String[] args) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        /* TODO: Implement the client here, according to your specification
         *   The client has to do the following:
         *   - connect to the server
         *   - initialize the dialog with the server according to your specification
         *   - In a loop:
         *     - read the command from the user on stdin (already created)
         *     - send the command to the server
         *     - read the response line from the server (using BufferedReader.readLine)
         */

        Socket clientSocket = null;
        BufferedWriter out = null;
        BufferedReader in = null;
        BufferedReader stdin;

        boolean exit = false;

        try {
            // Connect to the server
            clientSocket = new Socket( "127.0.0.1" /*"192.168.17.1"*/, 6996);

            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));

            // Initialize the dialog with the server
            String line = "";
            while (!line.startsWith("READY")) {
                while ((line = in.readLine()) != null)
                    LOG.log(Level.INFO, line);
            }
            System.out.println(line);

            while (!exit) {
                // Read the command from the user on stdin
                stdin = new BufferedReader(new InputStreamReader(System.in));
                String input = "";
                while (input.length() < 1)
                    input = stdin.readLine();

                if (line.startsWith("EXIT"))
                    exit = true;

                // Send the command to the server
                out.write(input);
                out.flush();

                // Read the response line from the server
                while ((line = in.readLine()) != null)
                    LOG.log(Level.INFO, line);
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
                if (clientSocket != null && ! clientSocket.isClosed()) clientSocket.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.toString(), ex);
            }
        }
    }
}
