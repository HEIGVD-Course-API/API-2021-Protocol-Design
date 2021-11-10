package ch.heigvd.api.calc;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator server implementation - single threaded
 */
public class Server {

    private final static Logger LOG = Logger.getLogger(Server.class.getName());
    private final int PORT = 9907;

    BufferedReader reader = null;
    BufferedWriter writer = null;

    /**
     * Main function to start the server
     */
    public static void main(String[] args) throws IOException {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        (new Server()).start();
    }

    /**
     * Start the server on a listening socket.
     */
    private void start() throws IOException {
        /* TODO: implement the receptionist server here.
         *  The receptionist just creates a server socket and accepts new client connections.
         *  For a new client connection, the actual work is done by the handleClient method below.
         */

        LOG.info("Starting server...");

        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        try{
            serverSocket = new ServerSocket(PORT);
            while(true) {
                clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
            } catch(IOException ex){
            LOG.log(Level.SEVERE, ex.getMessage());
        } finally{
            clientSocket.close();
            serverSocket.close();
        }
    }

    /**
     * Handle a single client connection: receive commands and send back the result.
     *
     * @param clientSocket with the connection with the individual client.
     */
    private void handleClient(Socket clientSocket) throws IOException {

        try {

            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
            String line;
            String[] tokens;
            String operand;
            String result = "RESULT ";

            LOG.info("Reading until client sends BYE");

            writer.write("AVAILABLE OPERATION (+) (-) (*) (/)\r\n");
            writer.flush();

            while ((line = reader.readLine()) != null) {
                tokens = line.split(" ");
                if (line.equals("BYE\r\n")) {
                    break;
                }

                if (tokens.length != 4) {
                    writer.write("ERROR 400 SYNTAX ERROR\r\n");

                } else if (tokens[0].equals("COMPUTE")) {
                    operand = tokens[1];
                    if (operand.equals("+")) {
                        writer.write(result + (Integer.parseInt(tokens[2]) + Integer.parseInt(tokens[3]) + "\r\n"));

                    } else if (operand.equals("-")) {
                        writer.write(result + (Integer.parseInt(tokens[2]) - Integer.parseInt(tokens[3]) + "\r\n"));

                    } else if (operand.equals("*")) {
                        writer.write(result + (Integer.parseInt(tokens[2]) * Integer.parseInt(tokens[3]) + "\r\n"));

                    } else if (operand.equals("/")) {
                        writer.write(result + (Integer.parseInt(tokens[2]) / Integer.parseInt(tokens[3]) + "\r\n"));

                    } else {
                        writer.write("ERROR 300 UNKNOWN OPERATIONS\r\n");

                    }

                }
                writer.flush();
            }
            reader.close();
            writer.close();
            clientSocket.close();
        }catch (IOException ex){
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex1);
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex1);
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