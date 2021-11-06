package ch.heigvd.api.calc;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator client implementation
 */
public class Client {

    private static final Logger LOG = Logger.getLogger(Client.class.getName());
    private static final int PORT = 2021;
    private static final String[] REQUESTS = {"Hello", "Bye"};
    private static final String CRLF = "\r\n";
    private enum Request {START, END};
    private static final int BUFFER_SIZE = 32;


    private static BufferedWriter stdout = null;
    private static BufferedReader stdin = null;

    private static Socket clientSocket = null;
    private static OutputStream os = null;
    private static InputStream is = null;

    /**
     * Main function to run client
     *
     * @param args no args required
     */
    public static void main(String[] args) throws IOException {
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

        stdout = new BufferedWriter(new OutputStreamWriter(System.out));
        stdin = new BufferedReader(new InputStreamReader(System.in));

        try{
            // Establish connection with server and set streams to send/receive bytes
            clientSocket = new Socket(InetAddress.getLocalHost(), PORT);
            os = clientSocket.getOutputStream();
            is = clientSocket.getInputStream();

            // Start connection
            String connection = REQUESTS[Request.START.ordinal()] + CRLF;
            os.write(connection.getBytes(StandardCharsets.UTF_8));

            ByteArrayOutputStream responseBuffer = showServerResponse();

            // Checks if the server responded correctly to the request and
            if(Objects.equals(responseBuffer.toString(), REQUESTS[Request.START.ordinal()])){
                stdout.write("Operation syntax : operator number1 number2\n");
                stdout.write("operator : \"+\", \"*\"\n");
                stdout.write("To quit write : \"Bye\"\n\n");

                while (true){
                    // Reads the user request and send it to the server
                    String userRequest = getUserRequest();

                    // Sends the user request
                    os.write(userRequest.getBytes(StandardCharsets.UTF_8));

                    // Ends the connection if the client wants to
                    if(userRequest.equals(REQUESTS[Request.END.ordinal()] + CRLF))
                        break;

                    // Reads the server response
                    showServerResponse();
                }
            }

        } catch (IOException ex){
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            is.close();
            os.close();
            clientSocket.close();
            stdin.close();
            stdout.close();
        }
    }

    /**
     * Gets the request without checking if he correctly responded
     * @return User request
     * @throws IOException throws an error if there's any problem with the system in/output
     */
    private static String getUserRequest() throws IOException {
        stdout.write("Send the wanted operation : ");
        return stdin.readLine();
    }

    /**
     * Reads the server response and logs it
     * @return Server response
     * @throws IOException throws an error if there's any problem with input from the client socket
     */
    private static ByteArrayOutputStream showServerResponse() throws IOException {
        ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int newBytes;

        while ((newBytes = is.read(buffer)) != -1){
            responseBuffer.write(buffer, 0, newBytes);
        }

        LOG.log(Level.INFO, "Response sent by the server: ");
        LOG.log(Level.INFO, responseBuffer.toString());

        return responseBuffer;
    }
}
