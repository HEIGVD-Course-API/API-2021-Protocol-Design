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
    Socket clientSocket;
    BufferedReader in = null;
    PrintWriter out = null;
    private final static Logger LOG = Logger.getLogger(ServerWorker.class.getName());

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
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Run method of the thread.
     */
    @Override
    public void run() {


        try {
            out.println("Available operation are ADD, SUB, MULT, QUIT to left");
            out.flush();

            String clientInput = "";
            while(true) {
                // Wait for client request
                clientInput = in.readLine();
                if(clientInput.equals("QUIT")) {
                    System.out.println("Client has left conversation");
                    System.out.flush();
                    break;
                }

                // op contains the operation name followed by two integer operands
                String[] op = clientInput.split(" ");

                if(op.length != 3) {
                    out.println("Bad input");
                    out.flush();
                } else {
                    int n1 = Integer.parseInt(op[1]), n2 = Integer.parseInt(op[2]);
                    String result;
                    switch (op[0]) {
                        case "ADD":
                            result = Integer.toString(n1 + n2);
                            break;
                        case "SUB":
                            result = Integer.toString(n1 - n2);
                            break;
                        case "MULT":
                            result = Integer.toString(n1 * n2);
                            break;
                        default:
                            result = "Error while calculating";
                    }
                    out.println(result);
                    out.flush();
                }
            }

            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException ex) {

        }
    }
}