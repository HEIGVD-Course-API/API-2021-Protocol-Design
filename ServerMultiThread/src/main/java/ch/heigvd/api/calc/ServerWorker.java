package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator worker implementation
 */
public class ServerWorker implements Runnable {

    private final static Logger LOG = Logger.getLogger(ServerWorker.class.getName());
    Socket clientSocket;
    BufferedReader reader = null;
    BufferedWriter writer = null;

    /**
     * Instantiation of a new worker mapped to a socket
     *
     * @param clientSocket connected to worker
     */
    public ServerWorker(Socket clientSocket) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        /* TODO: prepare everything for the ServerWorker to run when the
         *   server calls the ServerWorker.run method.
         *   Don't call the ServerWorker.run method here. It has to be called from the Server.
         */

        try {
            this.clientSocket = clientSocket;
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
        }catch (IOException ex){
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * Run method of the thread.
     */
    @Override
    public void run() {

        /* TODO: implement the handling of a client connection according to the specification.
         *   The server has to do the following:
         *   - initialize the dialog according to the specification (for example send the list
         *     of possible commands)
         *   - In a loop:
         *     - Read a message from the input stream (using BufferedReader.readLine)
         *     - Handle the message
         *     - Send to result to the client
         */

        String line;
        String[] tokens;
        String operand;

        LOG.info("Reading until client sends BYE");

        try {

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
                        writer.write("RESULT " + (Integer.parseInt(tokens[2]) + Integer.parseInt(tokens[3]) + "\r\n"));

                    } else if (operand.equals("-")) {
                        writer.write("RESULT " + (Integer.parseInt(tokens[2]) - Integer.parseInt(tokens[3]) + "\r\n"));

                    } else if (operand.equals("*")) {
                        writer.write("RESULT " + (Integer.parseInt(tokens[2]) * Integer.parseInt(tokens[3]) + "\r\n"));

                    } else if (operand.equals("/")) {
                        writer.write("RESULT " + (Integer.parseInt(tokens[2]) / Integer.parseInt(tokens[3]) + "\r\n"));

                    } else {
                        writer.write("ERROR 300 UNKNOWN OPERATIONS\r\n");

                    }

                }
                writer.flush();
            }
            writer.close();
            reader.close();
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