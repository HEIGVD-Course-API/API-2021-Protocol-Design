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

        Socket clientSocket = null;
        BufferedWriter out = null;
        BufferedReader in = null;

        try {
            clientSocket = new Socket("10.192.93.80", 8069);
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            /*String malformedHttpRequest = "Hello, sorry, but I don't speak HTTP...\r\n\r\n";
            out.write(malformedHttpRequest);
            out.flush();*/
            int letter;

            LOG.log(Level.INFO, "*** Response sent by the server: ***");
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                if(line.equals("Type HELP to see example of commands.")){
                    break;
                }
            }
            boolean quit = false;
            while(!quit){
                out.write(stdin.readLine() + "\r\n");
                out.flush();
                while ((line = in.readLine()) != null) {
                    if(line.equals("> END TRANSMISSION")){
                        break;
                    }
                    if(line.equals("> Good bye!")){
                        quit = true;
                    }
                    System.out.println(line);
                }
            }




            //LOG.log(Level.INFO, "*** Response 2 sent by the server: ***");
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
