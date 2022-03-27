package ch.heigvd.api.calc;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * Calculator server implementation - single threaded
 */
public class Server {

    private final static Logger LOG = Logger.getLogger(Server.class.getName());
    private final static int PORT = 2400;

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
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return;
        }
        while (true) {
            LOG.info("Waiting (blocking) for a new client...");
            try {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
                clientSocket.close();
            } catch (IOException ex) {
                // LOG.log(Level.SEVERE, ex.getMessage, ex);
            }
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
        try(
         InputStream inputStream = clientSocket.getInputStream();
         OutputStream outputStream = clientSocket.getOutputStream();
        ) {
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            PrintWriter out = new PrintWriter(outputStream);
            out.println("WELCOME !");
            out.flush();
            while(true) {
                out.println("PLEASE ENTER COMMAND");
                out.flush();
                String line = in.readLine();
                if(line == null || line.toUpperCase().equals("QUIT"))
                    return;
                String[] instructions = Arrays.stream(
                        line.split(" ")
                ).filter(x -> !x.equals("") && !x.equals(" ") && x != null).toArray(String[]::new);
                // LOG.info("received:" + Arrays.toString(instructions));
                if(instructions.length < 2) {
                    out.println("UNRECOGNIZED COMMAND");
                    out.flush();
                    continue;
                }
                String action = instructions[0].toUpperCase();
                int[] values = Arrays.stream(
                        instructions, 1, instructions.length
                ).mapToInt(Integer::valueOf).toArray();
                // LOG.info("transformed:" + Arrays.toString(values));
                if(action.equals("ADD")) {
                    int res = Arrays.stream(values).sum();
                    // LOG.info("SUM: " + res);
                    out.println(res);
                    out.flush();
                } else if(action.equals("SUB")) {
                    int res = values[0] - Arrays.stream(values, 1, values.length).sum();
                    out.println(res);
                    out.flush();
                } else {
                    out.println("UNRECOGNIZED COMMAND");
                    out.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}