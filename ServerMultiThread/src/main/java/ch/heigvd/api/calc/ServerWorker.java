package ch.heigvd.api.calc;

import ch.heigvd.api.calc.operation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator worker implementation
 */
public class ServerWorker implements Runnable {

    private final static Logger LOG = Logger.getLogger(ServerWorker.class.getName());
    private static final Map<String, Operation> operations = new HashMap<>() {{
        put("add", new Addition());
        put("mul", new Multiplication());
        put("div", new Division());
        put("mod", new Modulus());
        put("factorial", new Factorial());
    }};
    private final static String END_LINE = "\r\n";

    private Socket clientSocket;
    private BufferedReader in = null;
    private PrintWriter out = null;

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

    private String errorMsg(int code, String message) {
        return String.format("err %d %s", code, message);
    }

    private void welcomeMsg() {
        out.printf("welcome%s", END_LINE);

        // Send all operations available
        out.printf("disp op%s", END_LINE);
        for (Map.Entry<String, Operation> operation : operations.entrySet()) {
            out.printf("%s %d%s", operation.getKey(), operation.getValue().getNbOperands(), END_LINE);
        }
        out.printf("end%s", END_LINE);
    }

    private String processCommand(String[] args) {
        // Verify that the operation exits.
        if (!operations.containsKey(args[0])) {
            return errorMsg(1, String.format("'%s' is not a known operation.", args[0]));
        }

        // Verify that the operation has the corresponding number of arguments.
        var op = operations.get(args[0]);
        if (op.getNbOperands() != args.length - 1) {
            return errorMsg(2, "Too few or too much operands.");
        }

        // Do the operation and return the result.
        try {
            return String.format("res %s", op.doOperation(args));
        } catch (Exception ex) {
            // Will catch general error, like parse error.
            return errorMsg(3, ex.getMessage());
        }
    }

    /**
     * Run method of the thread.
     */
    @Override
    public void run() {
        String line;

        try {
            // Send welcome message.
            welcomeMsg();
            out.flush();

            // Wait for client message.
            while ((line = in.readLine()) != null) {
                if (line.equalsIgnoreCase("bye")) {
                    break;
                }

                // Split the message to process it.
                String[] command = line.split(" ");

                // Do nothing if there's nothing.
                if (command.length == 0) {
                    continue;
                }

                // Process the command and send the response.
                out.printf("%s%s", processCommand(command), END_LINE);
                out.flush();
                out.println();
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            // Close all connections.
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
    }
}