package ch.heigvd.api.calc;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Double.parseDouble;

/**
 * Calculator server implementation - single threaded
 */
public class Server {

    private final static Logger LOG = Logger.getLogger(Server.class.getName());
    private final static int PORT = 4242;

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
        ServerSocket serverSocket;
        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return;
        }

        while (true) {
            try {
                LOG.log(Level.INFO, "Single-threaded: Waiting for a new client on port " + PORT);
                clientSocket = serverSocket.accept();
                handleClient(clientSocket);

            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

    }

    /**
     * Handle a single client connection: receive commands and send back the result.
     *
     * @param clientSocket with the connection with the individual client.
     */
    private void handleClient(Socket clientSocket) {
        BufferedWriter out = null;
        BufferedReader in = null;

        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));

            String line;

            out.write("Connected to the online calculator.\n Possible requests are :\nADD X Y\nSUB X Y\nMULT X Y\nDIV X Y\nQUIT\nWaiting for a request...\n");
            out.flush();

            boolean quit = false;

            LOG.info("Reading until client sends QUIT or closes the connection...");
            while (!quit) {
                line = in.readLine();
                LOG.log(Level.INFO, line);
                String[] request = line.toLowerCase().split(" ");
                if (request.length > 3 || request.length == 2) {
                    out.write("ERROR : wrong number of arguments\nWaiting for a request...\n");
                    out.flush();
                } else {
                    switch (request[0]) {
                        case "add":
                            if (!checkSyntax(request)) {
                                out.write("ERROR : wrong syntax \nWaiting for a request...\n");
                                out.flush();
                                break;
                            }
                            out.write("RESULT " + (parseDouble(request[1]) + parseDouble(request[2])) + "\nWaiting for a request...\n");
                            out.flush();
                            break;
                        case "sub":
                            if (!checkSyntax(request)) {
                                out.write("ERROR : wrong syntax \nWaiting for a request...\n");
                                out.flush();
                                break;
                            }
                            out.write("RESULT " + (parseDouble(request[1]) - parseDouble(request[2])) + "\nWaiting for a request...\n");
                            out.flush();
                            break;
                        case "mult":
                            if (!checkSyntax(request)) {
                                out.write("ERROR : wrong syntax \nWaiting for a request...\n");
                                out.flush();
                                break;
                            }
                            out.write("RESULT " + (parseDouble(request[1]) * parseDouble(request[2])) + "\nWaiting for a request...\n");
                            out.flush();
                            break;
                        case "div":
                            if (!checkSyntax(request)) {
                                out.write("ERROR : wrong syntax \nWaiting for a request...\n");
                                out.flush();
                                break;
                            }
                            out.write("RESULT " + (parseDouble(request[1]) / parseDouble(request[2])) + "\nWaiting for a request...\n");
                            out.flush();
                            break;
                        case "quit":
                            quit = true;
                            out.write("CLOSING\n");
                            out.flush();
                            break;
                        default:
                            out.write("ERROR : unknown command\nWaiting for a request...\n");
                            out.flush();
                            break;
                    }
                }
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
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private boolean checkSyntax(String[] request) {
        if (request.length != 3) {
            return false;
        }
        try {
            parseDouble(request[1]);
            parseDouble(request[2]);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}