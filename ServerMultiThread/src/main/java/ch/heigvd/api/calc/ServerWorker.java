package ch.heigvd.api.calc;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator worker implementation
 */
public class ServerWorker implements Runnable {

    private final static Logger LOG = Logger.getLogger(ServerWorker.class.getName());
    private static final String CRLF = "\r\n";


    Socket clientSocket;
    BufferedReader in = null;
    PrintWriter out = null;

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
        try {
            this.clientSocket = clientSocket;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
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


        String line;
        boolean shouldRun = true;
        LOG.info("Welcome to the Multi-Threaded Server.\nSend me your operation and conclude with the Bye command.");

        BufferedReader in = null;
        BufferedWriter out = null;

        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));

            LOG.info("Reading until client sends BYE or closes the connection...");
            while ((shouldRun) && (line = in.readLine()) != null) {
                LOG.log(Level.INFO, "Client sent : " + line);

                if (line.equalsIgnoreCase("Bye")) {
                    shouldRun = false;
                }

                if (line.equals("Hello")) {
                    out.write("Hello\n");
                    out.flush();
                    continue;
                }

                ArrayList<String> list = new ArrayList<>(List.of(line.split(" ")));

                int OPERATION_SIZE = 3;
                if (list.size() != OPERATION_SIZE) {
                    out.write("Bad syntax\n");
                    out.flush();
                } else {

                    String op = list.get(0);

                    if (!Objects.equals(op, "+") && !Objects.equals(op, "*")) {
                        out.write("Bad syntax\n");
                        out.flush();
                    }

                    int nb1;
                    int nb2;

                    try {
                        nb1 = Integer.parseInt(list.get(1));
                        nb2 = Integer.parseInt(list.get(2));
                    } catch (NumberFormatException ex) {
                        out.write("Bad syntax\n");
                        out.flush();
                        continue;
                    }

                    int result = 0;

                    if (Objects.equals(op, "+")) {
                        result = nb1 + nb2;
                    } else if (Objects.equals(op, "*")) {
                        result = nb1 * nb2;
                    }

                    out.write(result + CRLF);
                    out.flush();
                }
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
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
            try {
                clientSocket.close();
            } catch (IOException ex1) {
                LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
            }
        }


    }
}