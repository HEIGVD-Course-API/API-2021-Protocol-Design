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

    private Socket clientSocket;
    private BufferedReader in = null;
    private BufferedWriter out = null;

    /**
     * Instantiation of a new worker mapped to a socket
     *
     * @param clientSocket connected to worker
     */
    public ServerWorker(Socket clientSocket) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        this.clientSocket = clientSocket;
        try {
            in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    /**
     * Run method of the thread.
     */
    @Override
    public void run() {

        try {
            System.out.printf("START : Connection with %s\n", clientSocket.getInetAddress());

            out.write("[ACK] : Welcome!\r\n");
            out.flush();

            String query;
            String response;
            String[] content;

            // This represents the conversation's state:
            // 0 -> 2 : (in)validity of the operands
            // -1 : error while performing the computation
            // -2 : exiting
            byte state;

            int lhs = 0;
            int rhs = 0;
            int result = 0;
            while (!clientSocket.isClosed()) {
                query = in.readLine();
                content = query.split(" +");

                response = "";
                state = 0;
                if (content.length != 3) {
                    if (content.length == 1 && content[0].equals("exit")) {
                        response = "[ACK] : Goodbye!";
                        state = -2;
                    } else {
                        response = "[ERROR] : Bad expression, expected 'operator int1 int2'";
                    }
                } else {

                    // Operand 1
                    try {
                        lhs = Integer.parseInt(content[1]);
                        ++state;
                    } catch (NumberFormatException e) {
                        response = "[ERROR] : Left operand is not a valid integer";
                    }

                    // Operand 2
                    try {
                        rhs = Integer.parseInt(content[2]);
                        ++state;
                    } catch (NumberFormatException e) {
                        if (state == 1)
                            response = "[ERROR] : Right operand is not a valid integer";
                        else // ok == 0, meaning we were not able to read both operands
                            response = "[ERROR] : Both operands are not valid integers";
                    }

                    // Operator
                    if (state == 2) {
                        switch (content[0]) {
                            case "+":
                                result = lhs + rhs;
                                break;
                            case "-":
                                result = lhs - rhs;
                                break;
                            case "*":
                                result = lhs * rhs;
                                break;
                            case "/":
                                if (rhs == 0) {
                                    response = "[ERROR] : Division by zero";
                                    state = -1;
                                } else {
                                    result = lhs / rhs;
                                }
                                break;
                            default:
                                response = String.format("[ERROR] : Unknown operator '%s'", content[0]);
                                state = -1;
                                break;
                        }

                        // If no error occurred while performing the operation
                        if (state > -1)
                            response = String.format("[OK] : %d", result);
                    }
                }

                out.write(response + "\r\n");
                out.flush();

                // The client sent the 'exit' command
                if (state == -2) {
                    clientSocket.close();
                    break;
                }
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
        } catch (NullPointerException e) {
            System.out.println("The client may have closed the connection on its own... :(");
        } finally {

            // Closing the connections properly
            try {
                if (!clientSocket.isClosed())
                    clientSocket.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, null, e);
            }
            try {
                in.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, null, e);
            }
            try {
                out.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, null, e);
            }

            System.out.printf("END : Connection with %s\n", clientSocket.getInetAddress());
        }
    }
}
