package ch.heigvd.api.calc;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator server implementation - single threaded
 */
public class Server {
    final int PORT = 9999;

    private final static Logger LOG = Logger.getLogger(Server.class.getName());

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
        /* TODO: implement the receptionist server here.
         *  The receptionist just creates a server socket and accepts new client connections.
         *  For a new client connection, the actual work is done by the handleClient method below.
         */

        PrintWriter stdout = new PrintWriter(new OutputStreamWriter(System.out));

        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);
            stdout.println("Waiting for a new client\n");
            stdout.flush();

            clientSocket = serverSocket.accept();
            stdout.println("New client arrived!\n");
            stdout.flush();

            handleClient(clientSocket);

            stdout.close();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "failed to create new socket in Server function" + ex);
        }
    }

    /**
     * Handle a single client connection: receive commands and send back the result.
     *
     * @param clientSocket with the connection with the individual client.
     */
    private void handleClient(Socket clientSocket) {

        /* TODO: implement the handling of a client connection according to the specification.
         *   The server has to do the following:
         *   - initialize the dialog according to the specification (for example send the list
         *     of possible commands)
         *   - In a loop:
         *     - Read a message from the input stream (using BufferedReader.readLine)
         *     - Handle the message
         *     - Send to result to the client
         */
        PrintWriter stdout = null;
        PrintWriter out = null;
        BufferedReader in = null;


        try {
            stdout = new PrintWriter(new OutputStreamWriter(System.out));
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            in = new BufferedReader((new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8)));

            out.println("Available operation are ADD, SUB, MULT, QUIT to left");
            out.flush();

            String clientInput = "";
            while (true) {
//                out.println("Write an operation :");
//                out.flush();

                // Wait for client request
                clientInput = in.readLine();
                stdout.println("Input is : " + clientInput);
                stdout.flush();

                if(clientInput.equals("QUIT")) {
                    System.out.println("Client has left conversation");
                    System.out.flush();
                    break;
                }

                // op contains the operation name followed by two integer operands
                String[] op = clientInput.split(" ");

                if (op.length != 3) {
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
                    out.println("Result = " + result);
                    out.flush();
                }
            }


            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException ex) {


        }
    }
}