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
    private final static int PORT = 23000;

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

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);

            while (true) {
                clientSocket = serverSocket.accept();
                handleClient(clientSocket);
                clientSocket.close();
            }
        } catch (IOException ex){
            LOG.log(Level.SEVERE, ex.getMessage());
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
        BufferedReader in = null;
        BufferedWriter out = null;

        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));

            String line;
            //out.write("HELLO\n");
            LOG.log(Level.INFO, "Un client est connecté");
            out.flush();

            while (!(line = in.readLine()).equals("quit") ) {
                LOG.log(Level.INFO, "Le client à envoyé un calcul");

                String[] parts = line.split(" ");

                if (parts.length > 3) {
                    LOG.log(Level.SEVERE, "");
                }

                char operation = parts[1].charAt(0);
                double lhs = Double.parseDouble(parts[0]);
                double rhs = Double.parseDouble(parts[2]);
                double result = 0;

                switch (operation) {
                    case '/':
                        result = lhs / rhs;
                        break;
                    case '+':
                        result = lhs + rhs;
                        break;
                    case '*':
                        result = lhs * rhs;
                        break;
                    case '-':
                        result = lhs - rhs;
                        break;
                    default:
                        break;
                }

                out.write(result + "\n");
                out.flush();
            }
            out.flush();

            LOG.info("Cleaning resources");
            in.close();
            out.close();
        } catch (IOException ex) {
            LOG.info("Error");
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
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}