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

        /* TODO: prepare everything for the ServerWorker to run when the
         *   server calls the ServerWorker.run method.
         *   Don't call the ServerWorker.run method here. It has to be called from the Server.
         */
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
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
        try {
            String line;
            LOG.log(Level.INFO, "Open connection with client");

            out.write("--- Welcome to the calculator ---\n" +
                    "Please input your calculation :\r\n");
            out.flush();

            while ((line = in.readLine()) != null) {

                String[] parts = line.split(" ");

                if (parts.length != 3) {
                    out.write("Wrong calculation format\r\n");
                    out.write("Please input your calculation :\r\n");
                    out.flush();
                    continue;
                }

                char operation = parts[1].charAt(0);
                double lhs = Double.parseDouble(parts[0]);
                double rhs = Double.parseDouble(parts[2]);
                double result = 0;

                switch (operation) {
                    case '/':
                        result = lhs / rhs;
                        break;
                    case '+':
                        result = lhs + rhs;
                        break;
                    case '*':
                        result = lhs * rhs;
                        break;
                    case '-':
                        result = lhs - rhs;
                        break;
                    default:
                        break;
                }

                out.write(result + "\r\n");
                out.write("Please input your calculation :\r\n");
                out.flush();
            }

            LOG.info("Close connection with client");
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
                out.close();
            }
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}