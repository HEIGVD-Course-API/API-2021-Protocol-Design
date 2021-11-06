package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

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

        String EOL = "CRLF",
               END_OPE = " - END OPERATIONS",
               BEGIN_OPE = "AVALABLE OPERATOINS";
        Vector<String> ope_list = new Vector<String>();
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        int PORT = 3101;
        Socket clientSocket = null;
        BufferedWriter out = null;
        BufferedReader in = null;
        BufferedReader stdin = null;

        try {
            clientSocket = new Socket("localhost", PORT);
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

//            String malformedHttpRequest = "Hello, sorry, but I don't speak HTTP...\r\n\r\n";
//            out.write(malformedHttpRequest);
//            out.flush();

            boolean waitingOperations = false;
            LOG.log(Level.INFO, "*** Response sent by the server: ***");
            String line;
            while ((line = in.readLine()) != null) {
                LOG.log(Level.INFO, line);

                // Line complete
                if (line.contains(EOL)) {

                    if (line.contains(EOL)) {

                        // enlever crtr
                        // List of operatoins
                        if (line.contains(BEGIN_OPE))
                            waitingOperations = true;

                        if (line.contains(END_OPE))
                            waitingOperations = false;

                        if (waitingOperations)
                            ope_list.add(line);
                        else {
                            stdin = new BufferedReader(new InputStreamReader(System.in));
                            out.write(stdin.readLine());
                        }
                    }
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
                if (stdin != null) stdin.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.toString(), ex);
            }
            try {
                if (clientSocket != null && ! clientSocket.isClosed()) clientSocket.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.toString(), ex);
            }
        }
        /* TODO: Implement the client here, according to your specification
         *   The client has to do the following:
         *   - connect to the server
         *   - initialize the dialog with the server according to your specification
         *   - In a loop:
         *     - read the command from the user on stdin (already created)
         *     - send the command to the server
         *     - read the response line from the server (using BufferedReader.readLine)
         */


    }
}
