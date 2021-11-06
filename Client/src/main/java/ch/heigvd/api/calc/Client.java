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

    private enum Request {START, END}

    private static BufferedWriter stdout = null;
    private static BufferedReader stdin = null;

    private static Socket clientSocket = null;
    private static BufferedWriter os = null;
    private static BufferedReader is = null;

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

        stdout = new BufferedWriter(new OutputStreamWriter(System.out));
        stdin = new BufferedReader(new InputStreamReader(System.in));

        try {
            // Creates a stream socket and connects it to the specified port number at the specified IP address
            clientSocket = new Socket(InetAddress.getLocalHost(), PORT);
            os = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));

            LOG.log(Level.INFO, "Server exist");

            // Start connection
            // DO NOT FORGET TO WRITE AN CRLF
            String connection = REQUESTS[Request.START.ordinal()] + CRLF;
            os.write(connection);
            os.flush();

            LOG.log(Level.INFO, "Waiting for an answer");
            String serverResponse = showServerResponse();

            // Checks if the server responded correctly to the request and
            if (Objects.equals(serverResponse, REQUESTS[Request.START.ordinal()])) {
                stdout.write("Operation syntax : operator number1 number2\n");
                stdout.write("operator : \"+\", \"*\"\n");
                stdout.write("To quit write : \"Bye\"\n\n");
                stdout.flush();

                while (true) {
                    // Reads the user request and send it to the server
                    String userRequest = getUserRequest();

                    // Sends the user request
                    os.write(userRequest + CRLF);
                    os.flush();

                    // Ends the connection if the client wants to
                    if (userRequest.equals(REQUESTS[Request.END.ordinal()]))
                        break;

                    // Reads the server response
                    showServerResponse();
                }
            }

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
            if (stdin != null) {
                try {
                    stdin.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
            if (stdout != null) {
                try {
                    stdout.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
        }
    }

    /**
     * Gets the request without checking if he correctly responded
     *
     * @return User request
     * @throws IOException throws an error if there's any problem with the system in/output
     */
    private static String getUserRequest() throws IOException {
        stdout.write("Submit your request : ");
        stdout.flush();
        // Waits the stream
        while (!stdin.ready()) ;
        return stdin.readLine();
    }

    /**
     * Reads the server response and logs it
     *
     * @return Server response
     * @throws IOException throws an error if there's any problem with input from the client socket
     */
    private static String showServerResponse() throws IOException {
        String serverResponse = is.readLine();

        stdout.write("Server result : " + serverResponse);
        stdout.newLine();
        stdout.flush();
        return serverResponse;
    }
}
