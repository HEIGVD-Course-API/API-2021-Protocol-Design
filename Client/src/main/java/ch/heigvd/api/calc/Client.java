package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator client implementation
 */
public class Client {

    private static final Logger LOG = Logger.getLogger(Client.class.getName());
    private static final int PORT = 4242;
    private static final String IP_SERVER = "10.192.92.95";

    /**
     * Main function to run client
     *
     * @param args no args required
     */
    public static void main(String[] args) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        BufferedReader stdin = null;
        Socket clientSocket = null;
        BufferedWriter out = null;
        BufferedReader in = null;

        /* Implement the client here, according to your specification
         *   The client has to do the following:
         *   - connect to the server
         *   - initialize the dialog with the server according to your specification
         *   - In a loop:
         *     - read the command from the user on stdin (already created)
         *     - send the command to the server
         *     - read the response line from the server (using BufferedReader.readLine)
         */


        try {
            //connection to the server
            clientSocket = new Socket(IP_SERVER, PORT);
            //sender
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            //receiver from the server
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //receiver from the prompt
            stdin = new BufferedReader(new InputStreamReader(System.in));


            //hello from server
            String line = in.readLine();
            do {
                LOG.log(Level.INFO, line);
            } while (!Objects.equals(line = in.readLine(), "Waiting for a request..."));


            String LineSend;
            do {
                //LOG.log(Level.INFO, "*** Write and Sent command from the user ***");
                LineSend = stdin.readLine() + "\n";
                out.write(LineSend);
                out.flush();

                //LOG.log(Level.INFO, "*** Print command received from the server ***");

                line = in.readLine();
                do {
                    LOG.log(Level.INFO, line);
                } while (!Objects.equals(line = in.readLine(), "Waiting for a request...") || in.readLine() != null);

            } while (in.readLine() != null);

            in.close();
            out.close();
            clientSocket.close();

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
                if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.toString(), ex);
            }
        }
    }
}
