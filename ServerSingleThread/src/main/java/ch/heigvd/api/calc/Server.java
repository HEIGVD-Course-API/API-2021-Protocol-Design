package ch.heigvd.api.calc;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator server implementation - single threaded
 */
public class Server {

    private final static Logger LOG = Logger.getLogger(Server.class.getName());

    private final static String WELCOME = "WELCOME!";
    private final static String RUNNING = "Computing...";
    private final static String ERROR = "ERROR";
    private final static String RESULT = "RESULT";
    private final static String QUIT = "QUIT";

    private final int LISTEN_PORT = 2021;

    /**
     * Main function to start the server
     */
    public static void main(String[] args) throws Exception {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        (new Server()).start();
    }

    /**
     * Start the server on a listening socket.
     */
    private void start() throws Exception {
        /* TODO: implement the receptionist server here.
         *  The receptionist just creates a server socket and accepts new client connections.
         *  For a new client connection, the actual work is done by the handleClient method below.
         */
        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        LOG.info("Starting server...");

        try {
            LOG.log(Level.INFO, "Creating a server socket and binding it on port {0}", new Object[]{Integer.toString(LISTEN_PORT)});
            serverSocket = new ServerSocket(LISTEN_PORT);
            clientSocket = serverSocket.accept();

            handleClient(clientSocket);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error while creating and binding server!", e);
            return;
        }


    }

    /**
     * Handle a single client connection: receive commands and send back the result.
     *
     * @param clientSocket with the connection with the individual client.
     */
    private void handleClient(Socket clientSocket) throws IOException {

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
        BufferedWriter writer = null;

        String welcomeMessage = WELCOME + "\n";
        String specificationMessage = "The available operations are and their command:\n" +
                                         "Addition : ADD x y (x and y are numbers)\n" +
                                             "Multiplication : MULT x y \n";

        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
        writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));

        writer.write(welcomeMessage);
        writer.write(specificationMessage);
        writer.flush();

        String request;
        while(!(request = reader.readLine()).equals(QUIT)){
            String[] param;

            param = request.split(" ");

            if(param.length != 3) {

                char param1Verif = param[1].charAt(0);
                char param2Verif = param[2].charAt(0);

                if (Character.isDigit(param1Verif) && Character.isDigit(param2Verif)) {
                    int op1 = Integer.parseInt(param[1]);
                    int op2 = Integer.parseInt(param[2]);

                    switch (param[0]) {
                        case "ADD":
                            LOG.log(Level.INFO, RUNNING);
                            writer.write(RESULT + " : ");
                            writer.write(op1 + op2 + "\n");
                            break;
                        case "MULT":
                            LOG.log(Level.INFO, RUNNING);
                            writer.write(RESULT + " : ");
                            writer.write(op1 * op2 + "\n");
                            break;

                        default:
                            LOG.log(Level.INFO, ERROR);
                            break;
                    }
                } else {
                    LOG.log(Level.INFO, ERROR);
                }
            }
        }
        LOG.log(Level.INFO, "Client requested QUIT, closing service!");

        writer.close();
        reader.close();
        clientSocket.close();

    }
}