package ch.heigvd.api.calc;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

/**
 * Calculator server implementation - single threaded
 */
public class Server {

    private final static Logger LOG = Logger.getLogger(Server.class.getName());
    private static final String CRLF = "\r\n";

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
            serverSocket = new ServerSocket(7548);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return;
        }
        while (true) {
            LOG.log(Level.INFO, "Single-threaded: Waiting for a new client on port {0}", 7548);
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
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
            String line;

            LOG.info("Reading until client sends BYE or closes the connection...");
            while ((line = in.readLine()) != null) {
                LOG.log(Level.INFO, "Client sent : " + line);

                String[] response = line.split(" ");
                String opCode = response[0];
                // check for close...
                int result = 0;
                if(opCode.equals("MUL"))
                    result = 1;
                try {
                    for (int i = 1; i < response.length; i++) {
                        int operand = Integer.parseInt(response[i]);
                        switch(opCode){
                            case "ADD":
                                result += operand;
                                break;
                            case "SUB":
                                result -= operand;
                                break;
                            case "MUL":
                                result *= operand;
                                break;
                        }
                    }
                    out.write("RES " + result + '\n');
                    out.flush();
                } catch(Exception ex){
                    LOG.log(Level.SEVERE, "parsing error");
                    out.write("ERR " + result + '\n');
                    out.flush();
                }

            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
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
        }

    }
}