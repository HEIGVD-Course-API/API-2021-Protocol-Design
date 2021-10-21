package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator client implementation
 */
public class Client {

    private static final Logger LOG = Logger.getLogger(Client.class.getName());
    private static int PORT = 4242;
    /**
     * Main function to run client
     *
     * @param args no args required
     */
    public static void main(String[] args) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        BufferedReader stdin = null;
        BufferedWriter stdout = null;
        Socket clientSocket = null;

        /* TODO: Implement the client here, according to your specification
         *   The client has to do the following:
         *   - connect to the server
         *   - initialize the dialog with the server according to your specification
         *   - In a loop:
         *     - read the command from the user on stdin (already created)
         *     - send the command to the server
         *     - read the response line from the server (using BufferedReader.readLine)
         */
        try {
            System.out.println("Client: Initiating connection on " + PORT + "...");

            clientSocket = new Socket("localhost", PORT);

            if (clientSocket.isClosed()) {
                System.out.println("Connection is closed. Aborting...");
                return;
            }

            System.out.println(clientSocket.getInetAddress());
            stdin = new BufferedReader(new InputStreamReader(System.in));
            stdout = new BufferedWriter(new OutputStreamWriter(System.out));

            String line;

            while((line = stdin.readLine()) != null) {
                stdout.write(line);

                if (line.startsWith("QUIT"))
                    break;

                LOG.log(Level.INFO, "Server: " + stdin.readLine());
            }

            LOG.log(Level.INFO, "Client: QUIT command sent, quitting...");

        }
        catch (IOException e){
            LOG.log(Level.SEVERE, e.toString(), e);
        }
        finally {
            try {
                if (stdin != null) stdin.close();
            }
            catch (IOException e) {
                LOG.log(Level.SEVERE, e.toString(), e);
            }

            try {
                if (stdout != null) stdout.close();
            }
            catch (IOException e) {
                LOG.log(Level.SEVERE, e.toString(), e);
            }

            try {
                if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            }
            catch (IOException e) {
                LOG.log(Level.SEVERE, e.toString(), e);
            }
        }
    }
}