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

        BufferedReader stdin = null;
        Socket clientSocket = null;
        BufferedWriter out = null;
        BufferedReader in = null;

        String serverOutput = "";
        String userInput = "";

        try {
            clientSocket = new Socket("localhost", 9999);
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            stdin = new BufferedReader(new InputStreamReader(System.in));

            serverOutput = in.readLine();

            do
            {
                System.out.println(serverOutput);
            }
            while(serverOutput.equals("AVAILABLE OPERATION (+) (-) (*) (/)\r\n"));

            while(!userInput.equals("BYE")){
                    LOG.log(Level.INFO, serverOutput);
                    serverOutput = in.readLine();
                    userInput = stdin.readLine();
                    out.write(userInput);
                    out.flush();
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
                if (in != null) stdin.close();
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
