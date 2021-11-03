package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator worker implementation
 */
public class ServerWorker implements Runnable {
    
    private final static Logger LOG = Logger.getLogger(ServerWorker.class.getName());
    
    private Socket clientSocket;
    private BufferedReader in = null;
    private PrintWriter out = null;
    
    private final static String[] OPERATIONS = {"ADD", "SUB", "MULT", "DIV"};
    private final static String ERROR_MSG = "ERROR: Calculation impossible";
    private final static String END_WELCOME_MSG = "ERROR: Calculation impossible";
    private final static String EXIT_CMD = "QUIT";
    
    /**
     * Instantiation of a new worker mapped to a socket
     *
     * @param clientSocket connected to worker
     */
    public ServerWorker(Socket clientSocket) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");
        
        try {
            this.clientSocket = clientSocket;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Run method of the thread.
     */
    @Override
    public void run() {
        String line;
        boolean shouldRun = true;
        
        out.println("Welcome! Here are the available operations: (enter " + EXIT_CMD + " to leave)");
        for (String op : OPERATIONS) {
            out.println(op);
        }
        out.println(END_WELCOME_MSG);
        
        out.flush();
        try {
            LOG.info("Reading until client sends QUIT or closes the connection...");
            while (shouldRun && (line = in.readLine()) != null) {
                if (line.equalsIgnoreCase("quit")) {
                    shouldRun = false;
                    out.flush();
                    continue;
                }
                String[] args = line.split(" ");
                if (args.length != 3) {
                    out.println(ERROR_MSG);
                } else {
                    String op = args[0];
                    int n1 = Integer.parseInt(args[1]);
                    int n2 = Integer.parseInt(args[2]);
                    switch (op) {
                        case "ADD":
                            out.println(n1 + n2);
                            break;
                        case "SUB":
                            out.println(n1 - n2);
                            break;
                        case "MULT":
                            out.println(n1 * n2);
                            break;
                        case "DIV":
                            out.println(n2 == 0 ? ERROR_MSG : n1 / n2);
                            break;
                        default:
                            out.println(ERROR_MSG);
                    }
                }
                out.flush();
            }
            
            LOG.info("Cleaning up resources...");
            clientSocket.close();
            in.close();
            out.close();
            
        } catch (IOException ex) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, "In BufferedReader cannot be closed");
                }
            }
            if (out != null) {
                out.close();
            }
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, "ClientSocket cannot be closed");
                }
            }
            LOG.log(Level.SEVERE, "Global error: client made a hard disconnect");
        }
    }
}