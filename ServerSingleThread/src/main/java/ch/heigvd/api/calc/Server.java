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
    private final int LISTEN_PORT = 3000;

    private static final String WELCOME_MESS = "Welcome.\n";
    private static final String HELP_MESS = "HELP MENU\n" +
            "Syntax : OP N1 N2\n" +
            "Supported operations :\n" +
            "- ADD\n" +
            "- MULT\n" +
            "To end the connexion, enter END\n" +
            "To print the help menu, enter HELP\n";
    private final String ENTER_CALCUL_MESS = "Enter your calcul.\n";

    private final String OK_CODE = "[OK]";
    private final String ERR_CODE = "[ERR]";

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
        Socket clientSocket = null;

        LOG.info("Starting server...");
        try {
            ServerSocket serverSocket = new ServerSocket(LISTEN_PORT);

            while (true) {
                // Blocked until a connection is made
                clientSocket = serverSocket.accept();

                handleClient(clientSocket);

                clientSocket.close();
        }

        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage());
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

        LOG.info("Handle client");
        BufferedReader reader = null;
        BufferedWriter writer = null;

        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
        writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));

        while (!reader.readLine().equals("HELLO")) {}

        writer.write(WELCOME_MESS + HELP_MESS);
        writer.flush();

        String line;
        while (!(line = reader.readLine()).equals("END")) {
            writer.write(ENTER_CALCUL_MESS);
            writer.flush();

            String[] commands = line.split(" ");

            //check operation
            switch (commands[0]) {
                case "ADD" :
                    LOG.log(Level.INFO, "Add operation");
                    writer.write("The result is " + (Integer.parseInt(commands[1]) + Integer.parseInt(commands[2])) + "\n");
                    break;
                case "MULT" :
                    LOG.log(Level.INFO, "Mult operation");
                    writer.write("The result is " + (Integer.parseInt(commands[1]) * Integer.parseInt(commands[2])) + "\n");
                    break;
                case "HElP" :
                    LOG.log(Level.INFO, "Asked for help");
                    writer.write(HELP_MESS);
                    break;
            }

            writer.flush();

            writer.close();
            reader.close();
        }
    }
}