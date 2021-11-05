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
    BufferedReader reader;
    BufferedWriter writer;
    String request;
    Socket client;
    /**
     * Instantiation of a new worker mapped to a socket
     *
     * @param clientSocket connected to worker
     */
    public ServerWorker(Socket clientSocket)throws IOException {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
            client = clientSocket;
        }

        /* TODO: prepare everything for the ServerWorker to run when the
         *   server calls the ServerWorker.run method.
         *   Don't call the ServerWorker.run method here. It has to be called from the Server.
         */
        private boolean read(){
            try{
                request = reader.readLine();
            }catch (IOException ex){
                LOG.log(Level.SEVERE, "Error while reading", ex);
                return false;
            }
            return true;
        }
        private boolean write(double r){
            try {
                writer.write(r+"\n");
                writer.flush();
            }catch (IOException ex){
                LOG.log(Level.SEVERE, "Error while writing", ex);
                return false;
            }
            return true;
        }
        private void write(String s){
            try {
                writer.write(s+"\n");
                writer.flush();
            }catch (IOException ex){
                LOG.log(Level.SEVERE, "Error while writing", ex);
                return;
            }
        }
        private double compute(String[] tokens) throws Exception{
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
        double result = 0;
        do {
            // Read a message from the input stream (using BufferedReader.readLine)
            // and set the request variable to the message
            if(!read() || request == null || request.toLowerCase().contains("stop")){
                LOG.log(Level.SEVERE, "Error while reading");
                break;
            }
            try{
                result = compute(request.split(";"));
            }catch (Exception ex){
                LOG.log(Level.SEVERE, "Error while computing result", ex);
                write("Error while computing result"+ex.getMessage()+"please retry");
                continue;
            }
            /**
             * We don't want to continue if writing is not working
             */
            if(!write(result)){
                LOG.log(Level.SEVERE, "Error while writing");
            }

        }while(!client.isClosed());

    }
}