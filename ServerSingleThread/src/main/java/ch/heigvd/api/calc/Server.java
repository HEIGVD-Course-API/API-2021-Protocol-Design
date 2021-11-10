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
    int port = 4269;
    BufferedReader in = null;
    BufferedWriter out = null;
    ServerSocket serverSocket;
    Socket clientSocket = null;
    String regex = "^(\\d+[\\+\\-\\*\\/]{1})+\\d+$";

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
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return;
        }
        while (true) {
            try {
                LOG.log(Level.INFO, "Single-threaded: Waiting for a new client on port {0}", port);
                clientSocket = serverSocket.accept();

                handleClient(clientSocket);
                String line;
            }
            catch(Exception ex){
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

    /**
     * Handle a single client connection: receive commands and send back the result.
     *
     * @param clientSocket with the connection with the individual client.
     */
    private void handleClient(Socket clientSocket) throws IOException {
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));

        out.write("connexion etablie...\nOPE 1+2 pour une operation\nEXI pour quitter l'application\n");
        out.flush();

        String line;
        while ( (line = in.readLine()) != null ) {
            if (line.equalsIgnoreCase("END")) {
                break;
            }

            switch(line.substring(0,3)){
                case "OPE":
                    Integer result = calc(line.substring(4));
                    out.write(result == -1 ? "CAE : calcul error\n" : result.toString());
                    out.flush();
                    break;
                case "EXI":
                    out.write("EXS : cleaning resources...\n");
                    out.flush();
                    exit();
                    break;
                default:
                    out.write("COE : command error\n");
                    out.flush();
                    break;
            }
        }
    }

    /**
     * do the thing
     * @param a the thing
     * @return result of the thing
     */
    private int calc(String a){
        if(a.matches(regex)) {
            String[] operators = a.split("[0-9]+");
            String[] operands = a.split("[+-]");
            int agregate = Integer.parseInt(operands[0]);

            for (int i = 1; i < operands.length; i++) {
                if (operators[i].equals("+"))
                    agregate += Integer.parseInt(operands[i]);
                else
                    agregate -= Integer.parseInt(operands[i]);
            }
            return agregate;
        }
        return -1;
    }

    /**
     * commits hara-kiri
     */
    private void exit() throws IOException{
        clientSocket.close();
        in.close();
        out.close();
    }
}