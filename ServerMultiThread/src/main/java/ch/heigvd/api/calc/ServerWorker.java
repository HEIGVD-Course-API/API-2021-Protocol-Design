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

        /* TODO: prepare everything for the ServerWorker to run when the
         *   server calls the ServerWorker.run method.
         *   Don't call the ServerWorker.run method here. It has to be called from the Server.
         */
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
            out.write("[ACK] : Welcome!\r\n");
            out.flush();

            String query;
            String response;
            String[] content;

            // Represents the conversation's state:
            // 0 -> 2 : operand (in)validity
            // -1 : error while performing the computation
            // -2 : exiting
            short state;

            int lhs = 0;
            int rhs = 0;
            int result = 0;
            while (!clientSocket.isClosed()) {

                query = in.readLine();
                if (query == null)
                    break;

                System.out.println(query);
                content = query.split(" +");

                System.out.printf("content's content [%d]:\n", content.length);
                for (int i = 0; i < content.length; ++i)
                    System.out.printf("'%s' ", content[i]);
                System.out.println();

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
                    try {
                        lhs = Integer.parseInt(content[1]);
                        ++state;
                    } catch (NumberFormatException e) {
                        response = "[ERROR] : Left operand is not a valid integer";
                    }

                    try {
                        rhs = Integer.parseInt(content[2]);
                        ++state;
                    } catch (NumberFormatException e) {
                        if (state == 1)
                            response = "[ERROR] : Right operand is not a valid integer";
                        else // ok == 0, meaning we were not able to read both operands
                            response = "[ERROR] : Both operands are not valid integers";
                    }

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
                        if (state > -1)
                            response = String.format("[OK] : %d", result);
                    }
                }

                out.write(response + "\r\n");
                out.flush();
                if (state == -2) {
                    clientSocket.close();
                    break;
                }
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
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
        }

    }
}