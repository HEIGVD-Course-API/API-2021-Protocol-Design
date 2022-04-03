package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator worker implementation
 */
public class ServerWorker implements Runnable {

    private final static Logger LOG = Logger.getLogger(ServerWorker.class.getName());
    private final Socket clientSocket;
    private final BufferedReader input;
    private final BufferedWriter output;
    static final ArrayList<Operation> OPERATIONS = new ArrayList<>();

    /**
     * Instantiation of a new worker mapped to a socket
     *
     * @param socket connected to worker
     */
    public ServerWorker(Socket socket) throws IOException {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        clientSocket = socket;
        output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        output.flush();
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        /* TODO: prepare everything for the ServerWorker to run when the
         *   server calls the ServerWorker.run method.
         *   Don't call the ServerWorker.run method here. It has to be called from the Server.
         */

    }

    /**
     * Run method of the thread.
     */
    @Override
    public void run() {
        try {
            sendWelcome();
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Cannot send message");
        }

        while (!clientSocket.isClosed()) {
            try {
                String command = input.readLine();
                ClientCommand c = analyseCommand(command);
                switch (c) {
                    case GOODBYYE:
                        clientSocket.close();
                        break;
                    case ADD:
                        sendResult(new AddOperation(), command);
                        break;
                    case SUB:
                        sendResult(new SupOperation(), command);
                        break;
                    case MUL:
                        sendResult(new MulOperation(), command);
                        break;
                    case OTHER:
                        sendError("Wrong command");
                        break;
                }
            } catch (IOException e) {
                LOG.log(Level.WARNING, "Cannot send message");

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

    private void sendError(String message) throws IOException {
        output.write(new ErrorCommand(message) + "\r\n");
        output.flush();
    }

    private void sendResult(Operation operation, String command) throws IOException {
        String[] sOperands = command.substring(4).split(" ");
        double[] dOperands = new double[sOperands.length];

        try {
            for (int i = 0; i < sOperands.length; ++i) {
                dOperands[i] = Double.parseDouble(sOperands[i]);
            }

            ServerCommand result = operation.applyOp(dOperands);
            output.write(result + "\r\n");
            output.flush();
        } catch (NumberFormatException e) {
            sendError("Wrong operand format");
        }
    }

    private void sendWelcome() throws IOException {
        output.write("WELCOME\r\n");
        output.write("OPERATIONS:\r\n");
        for (Operation op : OPERATIONS) {
            output.write("\t" + op + "\r\n");
        }
        output.write("END\r\n");
        output.flush();
    }

    private ClientCommand analyseCommand(String command) {
        if (command == null) {
            return ClientCommand.OTHER;
        }
        if (command.equals(ClientCommand.GOODBYYE.toString().toUpperCase())) {
            return ClientCommand.GOODBYYE;
        }
        switch (command.substring(0, 3)) {
            case "ADD":
                return ClientCommand.ADD;
            case "SUB":
                return ClientCommand.SUB;
            case "MUL" :
                return ClientCommand.MUL;
            default:
                return ClientCommand.OTHER;
        }

    }
}