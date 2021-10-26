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
    final static int BUFFER_SIZE = 1024;
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
        int portNumber = 2048;
        String host = "127.0.0.1";

        stdin = new BufferedReader(new InputStreamReader(System.in));

        Socket clientSocket = null;
        OutputStream os = null;
        InputStream is = null;
        String clientRequest = "";

        try {
            clientSocket = new Socket(host, portNumber);
            os = clientSocket.getOutputStream();
            is = clientSocket.getInputStream();

            ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int newBytes;

            while ((newBytes = is.read(buffer)) != -1) {
                responseBuffer.write(buffer, 0, newBytes);
                System.out.println(responseBuffer.toString());

                /*
                if(clientRequest.equals("BYE")) {
                    break;
                }
                */
                //System.out.println("AFTER WHILE LOOP");
                clientRequest = stdin.readLine();

                os.write(clientRequest.getBytes());
                //System.out.println("AFTER OS.WRITE");
                responseBuffer.reset();

            }
        }
        catch(IOException ex){
            LOG.log(Level.SEVERE, null, ex);
        }finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                os.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, "os no closed", ex);
            }
            try {
                clientSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}

