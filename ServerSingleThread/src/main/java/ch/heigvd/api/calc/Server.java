package ch.heigvd.api.calc;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator server implementation - single threaded
 */
public class Server {

    private final static Logger LOG = Logger.getLogger(Server.class.getName());

    /**
     * Main function to start the server
     */
    public static void main(String[] args) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        (new Server()).start();
    }

    /**
     * Start the server on a listening socket.
     */
    private void start() {
        /* TODO: implement the receptionist server here.
         *  The receptionist just creates a server socket and accepts new client connections.
         *  For a new client connection, the actual work is done by the handleClient method below.
         */

        System.out.println("Starting server...");

        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        int portNumber = 2048;

        try {
            // creates the socket and wait for the client connection
            serverSocket = new ServerSocket(portNumber);
            clientSocket = serverSocket.accept();

            // pass the socket to the handler
            handleClient(clientSocket);

        }catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        } finally {
            LOG.log(Level.INFO, "We are done. Cleaning up resources, closing streams and sockets...");
            try {
                clientSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null,
                        ex);
            }
            try {
                serverSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null,
                        ex);
            }
        }

    }

    /**
     * Handle a single client connection: receive commands and send back the result.
     *
     * @param clientSocket with the connection with the individual client.
     */
    private void handleClient(Socket clientSocket) {

        /* TODO: implement the handling of a client connection according to the specification.
         *   The server has to do the following:
         *   - initialize the dialog according to the specification (for example send the list
         *     of possible commands)
         *   - In a loop:
         *     - Read a message from the input stream (using BufferedReader.readLine)
         *     - Handle the message
         *     - Send to result to the client
         */

        BufferedReader reader = null;
        PrintWriter writer = null;
        InputStream fromClient = null;

        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream());

            // streams
            fromClient = clientSocket.getInputStream();

            String drawing = " _____________________\n" +
                    "|  _________________  |\n" +
                    "| | API CALC SERVER | |\n" +
                    "| |_________________| |\n" +
                    "|  ___ ___ ___   ___  |\n" +
                    "| | 7 | 8 | 9 | | + | |\n" +
                    "| |___|___|___| |___| |\n" +
                    "| | 4 | 5 | 6 | | - | |\n" +
                    "| |___|___|___| |___| |\n" +
                    "| | 1 | 2 | 3 | | x | |\n" +
                    "| |___|___|___| |___| |\n" +
                    "| | . | 0 | = | | / | |\n" +
                    "| |___|___|___| |___| |\n" +
                    "|_____________________|\n";


            // send a test WELCOME message
            String welcome = "ELHO \n" +
                    "- AVAILABLE COMMANDS\n" +
                    "- MATH\n" +
                    "\t- Description: perform math operation \n" +
                    "\t- Syntax: MATH OPERATION op1 op2 \n" +
                    "\t- Available OPERATION: \n" +
                    "\t\t- ADD \n" +
                    "\t\t- MULT \n" +
                    "\t\t- DIV \n" +
                    "\t\t- POW \n" +
                    "\t- EXAMPLE: MATH ADD 2 3\n" +
                    "- LIST \n" +
                    "\t- Description: display this message \n" +
                    "\t- Syntax: LIST\n" +
                    "\t- EXAMPLE: LIST\n" +
                    "END EHLO\n";

            writer.println(drawing + welcome);
            writer.flush();

            ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();

            final int BUFFER_SIZE = 1024;
            byte[] buffer = new byte[BUFFER_SIZE];
            int newBytes;
            String error = "Error\n";
            String resultStr = "RESULT: ";

            System.out.println("Reading server message...\n");
            while ((newBytes = fromClient.read(buffer)) != -1) {

                // process WELCOME message from the server

                responseBuffer.write(buffer, 0, newBytes);

                // converts the command sent by the client into an array of
                // words separated by anything that's not A-Z or 0-9
                String[] words = responseBuffer.toString().split("[^A-Z0-9]+");

                if(words.length == 0) {
                    words = Arrays.copyOf(words, 1);
                    words[0] = "";
                }

                // Quit if command was BYE
                if(words[0].equals("BYE")) {
                    //writer.println("CLOSING CONNECTION");
                    //writer.flush();
                    break;
                }

                // Process the command
                switch(words[0]){
                    case "MATH":
                        double result;
                        switch(words[1]) {
                            case "ADD":
                                result =  Integer.parseInt(words[2]) + Integer.parseInt(words[3]);
                                writer.println(resultStr + result);
                                writer.flush();
                                break;
                            case "MULT":
                                result =  Integer.parseInt(words[2]) * Integer.parseInt(words[3]);
                                writer.println(resultStr + result);
                                writer.flush();
                                break;
                            case "POW":
                                result = Math.pow(Integer.parseInt(words[2]), Integer.parseInt(words[3]));
                                writer.println(resultStr + result);
                                writer.flush();
                                break;
                            default:
                                writer.println(error + " wrong operands");
                                writer.flush();
                                break;
                        }
                        break;
                    case "LIST":
                        writer.println(welcome);
                        writer.flush();
                        break;
                    default:
                        writer.println(error + " wrong command");
                        writer.flush();
                }
                responseBuffer.reset();
            }


    } catch (IOException ex) {
        LOG.log(Level.SEVERE, ex.getMessage());
    } finally {
        LOG.log(Level.INFO, "We are done. Cleaning up resources, closing streams and sockets...");
        try {
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null,
                    ex);
        }

        writer.close();
        try {
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
    }
    }
}