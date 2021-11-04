package ch.heigvd.api.calc;

import ch.heigvd.api.calc.operation.Addition;
import ch.heigvd.api.calc.operation.Multiplication;
import ch.heigvd.api.calc.operation.Operation;

import java.io.*;
import java.net.Socket;
import java.util.*;
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

    private static final Map<String, Operation> operations = new HashMap<>() {{
        put("add", new Addition());
        put("mul", new Multiplication());
    }};

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
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * Run method of the thread.
     */
    @Override
    public void run() {
        String line;

        try {
            out.println("welcome");
            out.println("disp op");
            for (Map.Entry<String, Operation> operation : operations.entrySet()) {
                out.println(operation.getKey() + " " + operation.getValue().getNbOperands());
            }
            out.println("end");
            out.flush();

            while((line = in.readLine()) != null) {
                if (line.equalsIgnoreCase("bye")) {
                    break;
                }

                String[]  command = line.split(" ");
                if (operations.containsKey(command[0])) {
                    var op = operations.get(command[0]);
                    if (op.getNbOperands() == command.length - 1) {
                        out.println(op.doOperation(command));
                    }
                } else {

                }
                out.flush();
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }

            if (out != null) {
                out.close();
            }

            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }

        /* TODO: implement the handling of a client connection according to the specification.
         *   The server has to do the following:
         *   - initialize the dialog according to the specification (for example send the list
         *     of possible commands)
         *   - In a loop:
         *     - Read a message from the input stream (using BufferedReader.readLine)
         *     - Handle the message
         *     - Send to result to the client
         */

    }
}