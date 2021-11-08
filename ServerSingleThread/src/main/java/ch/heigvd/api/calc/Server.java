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

        /* TODO: implement the handling of a client connection according to the specification.
         *   The server has to do the following:
         *   - initialize the dialog according to the specification (for example send the list
         *     of possible commands)
         *   - In a loop:
         *     - Read a message from the input stream (using BufferedReader.readLine)
         *     - Handle the message
         *     - Send to result to the client
         */

        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
        String line;
        String[] tokens;
        String operand;

        LOG.info("Reading until client sends BYE");

        writer.write("AVAILABLE OPERATION (+) (-) (*) (/)");
        writer.flush();

        while ((line = reader.readLine()) != null) {
            tokens = line.split(" ");
            if (line.equals("BYE CLRF")) {
                break;
            }

            if(tokens.length != 4){
                writer.write("ERROR 400 SYNTAX ERROR");

            }
            else if(tokens[0].equals("COMPUTE")){
                operand = tokens[1];
                if(operand.equals("+")){
                    writer.write("RESULT " + (Integer.parseInt(tokens[2]) + Integer.parseInt(tokens[3])));

                }
                else if(operand.equals("-")){
                    writer.write("RESULT " + (Integer.parseInt(tokens[2]) - Integer.parseInt(tokens[3])));

                }
                else if(operand.equals("*")){
                    writer.write("RESULT " + (Integer.parseInt(tokens[2]) * Integer.parseInt(tokens[3])));

                }
                else if(operand.equals("/")){
                    writer.write("RESULT " + (Integer.parseInt(tokens[2]) / Integer.parseInt(tokens[3])));

                }
                else{
                    writer.write("ERROR 300 UNKNOWN OPERATIONS");

                }

            }
            writer.flush();
        }
    }
}