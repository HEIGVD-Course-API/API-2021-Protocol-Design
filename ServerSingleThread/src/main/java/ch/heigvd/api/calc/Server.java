package ch.heigvd.api.calc;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;
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
        try {
            (new Server()).start();
        }catch (Exception e){
            LOG.log(Level.SEVERE, "Error while starting the server", e);
        }
    }

    /**
     * Start the server on a listening socket.
     */
    private void start()  {
        /* TODO: implement the receptionist server here.
         *  The receptionist just creates a server socket and accepts new client connections.
         *  For a new client connection, the actual work is done by the handleClient method below.
         */
        ServerSocket server = null;
        try {
             server = new ServerSocket(2341);
        }catch (IOException e){
            LOG.log(Level.SEVERE, "Error while creating the server socket", e);
            return;
        }

        while (true) {
            Socket client = null;

            try {
                client = server.accept();
            }catch (IOException e) {
                LOG.log(Level.SEVERE, "Error while accepting a client", e);
                return;
            }
            handleClient(client);
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
        BufferedWriter writer = null;
        String rl =null;
        String request = null;
        do {
            try {
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
                writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
                request = reader.readLine();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Error while reading from client", e);
                return;
            }
            assert request != null;
            String[] tokens = request.split(";");
            double result = 0.0;
            try {
                result = compute(tokens);
                writer.write(String.valueOf(result)+'\n');
                writer.flush();
            }catch (Exception e){
                LOG.log(Level.SEVERE, "Error while computing", e);
                try{
                    writer.write("Error while computing, "+e.getMessage()+'\n');
                    writer.flush();
                }catch (IOException e1){
                    LOG.log(Level.SEVERE, "Error while writing to client", e1);
                    return;
                }
            }
            try{
                rl = reader.readLine();
            }catch (IOException e){
                LOG.log(Level.SEVERE, "Error while reading from client", e);
                return;
            }
        } while ( rl.toLowerCase().contains("stop"));
        try {
            clientSocket.close();
        }catch (IOException e){
            LOG.log(Level.SEVERE, "Error while closing the client socket", e);
        }
    }

    private static double compute(String[] tokens) throws Exception {
        switch (tokens[0]) {
            case "+":
                return Double.parseDouble(tokens[1]) + Double.parseDouble(tokens[2]);
            case "-":
                return Double.parseDouble(tokens[1]) - Double.parseDouble(tokens[2]);
            case "*":
                return Double.parseDouble(tokens[1]) * Double.parseDouble(tokens[2]);
            case "/":
                return Double.parseDouble(tokens[1]) / Double.parseDouble(tokens[2]);
            case "^":
                return (int) Math.pow(Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]));
            default:
                throw new Exception("Invalid command");
        }
    }
}