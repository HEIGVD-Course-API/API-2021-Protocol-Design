package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator worker implementation
 */
public class ServerWorker implements Runnable {

    private final static Logger LOG = Logger.getLogger(ServerWorker.class.getName());
    private Socket socket;

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
        socket = clientSocket;

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
                double[] values = null;
                try {
                    values = Arrays.stream(
                            instructions, 1, instructions.length
                    ).mapToDouble(Double::valueOf).toArray();
                } catch (Exception e) {
                    out.println("Conversion ERROR");
                    out.flush();
                    continue;
                }
                // LOG.info("transformed:" + Arrays.toString(values));
                if(action.equals("ADD")) {
                    double res = Arrays.stream(values).sum();
                    // LOG.info("SUM: " + res);
                    out.println(res);
                    out.flush();
                } else if(action.equals("SUB")) {
                    double res = values[0] - Arrays.stream(values, 1, values.length).sum();
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

    /**
     * Run method of the thread.
     */
    @Override
    public void run() {

        /* TODO: implement the handling of a client connection according to the specification.
         *   The server has to do the following:
         *   - initialize the dialog according to the specification (for example send the list
         *     of possible commands)
         *   - In a loop:
         *     - Read a message from the input stream (using BufferedReader.readLine)
         *     - Handle the message
         *     - Send to result to the client
         */

        handleClient(this.socket);

    }
}