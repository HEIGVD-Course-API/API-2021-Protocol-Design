package ch.heigvd.api.calc;

import javax.naming.directory.InvalidAttributeValueException;
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
    private final int PORT = 4242;
    private final String RESULT = "RESULT IS ";
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
        System.out.println("Starting server ...");

        ServerSocket serverSocket;
        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Socket created " + serverSocket.getLocalSocketAddress());
        } catch (IOException e){
            LOG.log(Level.SEVERE, null, e);
            return;
        }

        while(true) {
            try{
                LOG.log(Level.INFO, "waiting for new client");
                clientSocket = serverSocket.accept();
                if(clientSocket.isClosed()) {
                    System.out.println("client socket is closed");
                }
                handleClient(clientSocket);
            } catch (IOException e) {
                LOG.log(Level.SEVERE, e.toString(), e);
            } finally { // Is finally necessary ? Everything is in catch in the
                // example
                try {
                    if(clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, e.toString(), e);
                }
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


            String welcome = "WELCOME \nSYNTAX: REQUEST {OP} {V1} {V2} \n" +
                    "AVAILABLE_OPS \nADD V1 V2 \nMUL V1 V2 \nEND_OF_OPS \n";
            out.write(welcome);
            out.flush();
            System.out.println("Message sent");


            String line;
            while ( (line = in.readLine()) != null ) {
                if (line.equalsIgnoreCase("END")) {
                    out.write("END_OF_COMMUNICATION...BYE\n");
                    out.flush();
                    break;
                }
                String[] parts = line.split(" ");
                String result = "";
                switch (parts[0]) {
                    case "ADD":
                        try {
                            result = add(Integer.parseInt(parts[1]),
                                    Integer.parseInt(parts[2]));
                        } catch (InvalidAttributeValueException e) {
                            LOG.log(Level.SEVERE, e.toString(), e);
                        }
                        break;
                    case "MUL":
                        try {
                            result = multiply(Integer.parseInt(parts[1]),
                                    Integer.parseInt(parts[2]));
                        } catch (InvalidAttributeValueException e) {
                            LOG.log(Level.SEVERE, e.toString(), e);
                        }
                        break;
                }
                out.write(result);
                out.flush();
            }

            LOG.info("Cleaning up resources...");
            clientSocket.close();
            in.close();
            out.close();

        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.toString(), e);
        } finally {
            try {
                if(in != null) in.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, e.toString(), e);
            }
            try {
                if(out != null) out.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, e.toString(), e);
            }
        }
    }

    private String add(int v1, int v2) throws InvalidAttributeValueException {
        return RESULT + (v1 + v2) + '\n';
    }

    private String multiply(int v1, int v2) throws InvalidAttributeValueException {
        return RESULT + (v1 * v2) + '\n';
    }
}