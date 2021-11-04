package ch.heigvd.api.calc;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Integer.parseInt;

/**
 * Calculator server implementation - single threaded
 */
public class Server {

    private final static Logger LOG = Logger.getLogger(Server.class.getName());
    private final static int PORT = 4242;

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
        ServerSocket serverSocket;
        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return;
        }

        while (true) {
            try {
                LOG.log(Level.INFO, "Single-threaded: Waiting for a new client on port {0}", PORT);
                clientSocket = serverSocket.accept();

                handleClient(clientSocket);

            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
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

        BufferedWriter out = null;
        BufferedReader in = null;

        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));

            String line;

            out.write("Connected to the online calculator.\n" +
                    "Possible requests are :\n " +
                    "ADD X Y\n" +
                    "SUB X Y\n" +
                    "MULT X Y\n" +
                    "DIV X Y\n" +
                    "QUIT\n" +
                    "Waiting for a request...\n");

            out.flush();

            boolean quit = false;

            LOG.info("Reading until client sends QUIT or closes the connection...");
            while ((line = in.readLine()) != null && !quit) {
                String[] request = line.toLowerCase().split(" ");
                if (request.length > 3 || request.length == 2) {
                    out.write("ERROR : wrong number of arguments\n");
                    out.flush();
                    break;
                }
                switch (request[0]) {
                    case "add":
                        if (!checkSyntax(request)) {
                            out.write("ERROR : wrong syntax \n");
                            out.flush();
                        }
                        out.write("RESULT " + (parseInt(request[1]) + parseInt(request[2])) + "\n");
                        out.flush();
                        break;
                    case "sub":
                        if (!checkSyntax(request)) {
                            out.write("ERROR : wrong syntax \n");
                            out.flush();
                        }
                        out.write("RESULT " + (parseInt(request[1]) - parseInt(request[2])) + "\n");
                        out.flush();
                        break;
                    case "mult":
                        if (!checkSyntax(request)) {
                            out.write("ERROR : wrong syntax \n");
                            out.flush();
                        }
                        out.write("RESULT " + (parseInt(request[1]) * parseInt(request[2])) + "\n");
                        out.flush();
                        break;
                    case "div":
                        if (!checkSyntax(request)) {
                            out.write("ERROR : wrong syntax \n");
                            out.flush();
                        }
                        out.write("RESULT " + (parseInt(request[1]) / parseInt(request[2])) + "\n");
                        out.flush();
                        break;
                    case "quit":
                        quit = true;
                        out.write("CLOSING\n");
                        out.flush();
                        break;
                    default:
                        out.write("ERROR : unknown command\n");
                        out.flush();
                        break;
                }
            }

            LOG.info("Cleaning up resources...");
            clientSocket.close();
            in.close();
            out.close();

        } catch (IOException ex) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private boolean checkSyntax(String[] request) {
        if (request.length != 3) {
            return false;
        }
        try {
            parseInt(request[1]);
            parseInt(request[2]);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}