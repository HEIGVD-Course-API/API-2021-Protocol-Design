package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator worker implementation
 */
public class ServerWorker implements Runnable {

    private final static Logger LOG = Logger.getLogger(ServerWorker.class.getName());

    private Socket clientSocket;
    private BufferedReader reader = null;
    private PrintWriter writer = null;
    InputStream fromClient = null;

    /**
     * Instantiation of a new worker mapped to a socket
     *
     * @param clientSocket connected to worker
     */
    public ServerWorker(Socket clientSocket) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        /* TODO: prepare everything for the ServerWorker to run when the
         *   server calls the ServerWorker.run method.
         *   Don't call the ServerWorker.run method here. It has to be called from the Server.
         */
        this.clientSocket = clientSocket;

        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream());
            fromClient = clientSocket.getInputStream();
        }
        catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        }
        System.out.println("Worker initialized");
    }

    /**
     * Run method of the thread.
     */
    @Override
    public void run() {

        /* TODO: implement the handling of a client connection according to the specification.
         *   The server has to do the following:
         *   - initialize the dialog according to the specification (for example send the list
         *     of possible commands)
         *   - In a loop:
         *     - Read a message from the input stream (using BufferedReader.readLine)
         *     - Handle the message
         *     - Send to result to the client
         */
        try {
                String drawing = " _____________________\n" +
                        "|  _________________  |\n" +
                        "| | API CALC SERVER | |\n" +
                        "| |   MULTITHREAD   | |\n" +
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

                    if (words.length == 0) {
                        words = Arrays.copyOf(words, 1);
                        words[0] = "";
                    }

                    // Quit if command was BYE
                    if (words[0].equals("BYE")) {
                        //writer.println("CLOSING CONNECTION");
                        //writer.flush();
                        break;
                    }

                    // Process the command
                    switch (words[0]) {
                        case "MATH":
                            double result;
                            switch (words[1]) {
                                case "ADD":
                                    result = Integer.parseInt(words[2]) + Integer.parseInt(words[3]);
                                    writer.println(resultStr + result);
                                    writer.flush();
                                    break;
                                case "MULT":
                                    result = Integer.parseInt(words[2]) * Integer.parseInt(words[3]);
                                    writer.println(resultStr + result);
                                    writer.flush();
                                    break;
                                case "POW":
                                    result = Math.pow(Integer.parseInt(words[2]), Integer.parseInt(words[3]));
                                    writer.println(resultStr + result);
                                    writer.flush();
                                    break;
                                case "DIV":
                                    if(Double.parseDouble(words[3]) == 0.){
                                        writer.println(error + " cannot divide by 0");
                                        writer.flush();
                                        break;
                                    }
                                    result = Double.parseDouble(words[2]) / Double.parseDouble(words[3]);
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