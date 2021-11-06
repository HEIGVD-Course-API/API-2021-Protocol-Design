package ch.heigvd.api.calc;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

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
        ServerSocket serverSocket;
        Socket clientSocket;
        try {
            serverSocket = new ServerSocket(2021);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return;
        }
        while (true) {
            LOG.log(Level.INFO, "Single-threaded: Waiting for a new client on port {0}", 2021);
            try {
                clientSocket = serverSocket.accept();
                LOG.info("A new client has arrived. Starting a new thread and delegating work to a new servant...");
                handleClient(clientSocket);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
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

        BufferedReader in = null;
        BufferedWriter out = null;

        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
            String line;

            LOG.info("Reading until client sends BYE or closes the connection...");
            while ((line = in.readLine()) != null) {
                if (line.equalsIgnoreCase("Bye")) {
                    break;
                }
                if (line.equals("Hello")) {
                    out.write("Hello\n");
                    out.flush();
                }
                ArrayList<String> list = new ArrayList<>(Arrays.asList(line.split(" ")));
                int OPERATION_SIZE = 3;
                if (list.size() != OPERATION_SIZE) {
                    out.write("Bad syntax\n");
                    out.flush();
                } else {

                    String op = list.get(0);

                    if (!Objects.equals(op, "+") && !Objects.equals(op, "*")) {
                        out.write("Bad syntax\n");
                        out.flush();
                    }

                    int nb1 = Integer.getInteger(list.get(1));
                    int nb2 = Integer.getInteger(list.get(2));

                    int result = 0;

                    if (Objects.equals(op, "+")) {
                        result = nb1 + nb2;
                    } else if (Objects.equals(op, "×")) {
                        result = nb1 * nb2;
                    }

                    out.write(result);
                    out.flush();
                }
                // TODO: 06.11.21 verifier que ce sont bien des chiffres.

            }
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
            try {
                clientSocket.close();
            } catch (IOException ex1) {
                LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
            }
            LOG.log(Level.SEVERE, ex.getMessage(), ex);


        }

    }
}