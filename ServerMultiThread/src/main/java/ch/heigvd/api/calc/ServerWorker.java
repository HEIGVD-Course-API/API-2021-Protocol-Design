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
            System.out.println("Coucou je suis le run du thread");
            String s = "";
            String[] content = new String[]{};
            int i = 0;
            while (!clientSocket.isClosed()) {
                System.out.println(++i);
                s = in.readLine();
                if (s == null)
                    break;
                System.out.println(s);
                content = s.split(" +");
                if (content.length != 0 && content.length != 3) {
                    System.out.println("[ERROR]");
                    out.write("[ERROR] : Wrong expression\r\n");
                    out.flush();
                }
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
        } finally {
            // Closing the connections properly
            try {
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
            System.out.println("Le thread dit bye-bye");
        }

    }
}