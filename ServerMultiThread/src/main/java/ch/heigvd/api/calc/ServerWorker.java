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

    private final Socket clientSocket;
    private final BufferedReader in;
    private final BufferedWriter out;

    private final String[] SUPPORTED_OPERATIONS = {
            "ADD",
            "SUB",
            "MULT",
            "DIV"
    };

    /**
     * Instantiation of a new worker mapped to a socket
     *
     * @param clientSocket connected to worker
     */
    public ServerWorker(Socket clientSocket) throws IOException {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        this.clientSocket = clientSocket;
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
    }

    /**
     * Run method of the thread.
     */
    @Override
    public void run() {
        try {
            sendReady();

            while (!clientSocket.isClosed()) {
                while (in.ready()) {
                    String line = in.readLine();
                    LOG.log(Level.INFO, "Received: " + line);
                    String[] operations = line.split("\\s+");

                    if (operations.length < 1) {
                        sendError("Missing operation.");
                        break;
                    }

                    switch (operations[0]) {
                        case "EXIT":
                            return;
                        case "DO":
                            processDo(operations);
                            break;
                        default:
                            sendError("Unknown operation requested " + operations[0]);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error while processing request.", e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Error while closing BufferedReader.", e);
            }
            try {
                out.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Error while closing BufferedWriter.", e);
            }
            try {
                clientSocket.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Error while closing socket.", e);
            }
        }
    }

    private void sendReady() throws IOException {
        StringBuilder response = new StringBuilder("READY\nOperations:\n");
        for (String op : SUPPORTED_OPERATIONS) {
            response.append("- ").append(op).append("\n");
        }
        response.append("\n");
        out.write(response.toString());
        out.flush();
        LOG.log(Level.INFO, "Sent: " + response);
    }

    private void sendError(String message) throws IOException {
        String response = "ERROR\n" + message + "\n\n";
        out.write(response);
        out.flush();
        LOG.log(Level.INFO, "Sent: " + response);
    }

    private void processDo(String[] tokens) throws IOException {
        if (tokens.length != 4) {
            sendError("Invalid syntax for DO command.");
            return;
        }

        int a, b;
        String op = tokens[2];
        try {
            a = Integer.parseInt(tokens[1]);
            b = Integer.parseInt(tokens[3]);
        } catch (NumberFormatException e) {
            sendError("Invalid operand received.");
            return;
        }

        switch (op) {
            case "ADD":
                sendResult(a + b);
                break;
            case "SUB":
                sendResult(a - b);
                break;
            case "MULT":
                sendResult(a * b);
                break;
            case "DIV":
                if (b == 0) {
                    sendError("Invalid division by zero.");
                    return;
                }
                sendResult(a / b);
                break;
            default:
                sendError("Opération " + op + " non supportée");
                break;
        }
    }

    private void sendResult(int result) throws IOException {
        String response = "RESULT " + result + "\n";
        out.write(response);
        out.flush();
        LOG.log(Level.INFO, response);
    }
}