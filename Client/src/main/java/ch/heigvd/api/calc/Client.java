package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator client implementation
 */
public class Client {

    private static final Logger LOG = Logger.getLogger(Client.class.getName());

    /**
     * Main function to run client
     *
     * @param args no args required
     */
    public static void main(String[] args) throws IOException {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        BufferedReader stdin;

        Socket socket = null;
        try {
            LOG.info("Connecting to server on localhost:42069");
            socket = new Socket("localhost", 42069);
            LOG.info("Connected to server on localhost:42069");
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Could not connect to socket (" + e.getMessage() + "), exiting");
            System.exit(1);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter bwt = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

        stdin = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = stdin.readLine()) != null) {

            if (line.equals("QUIT")) {
                LOG.info("Exit requested");
                break;
            }

            bwt.write(line + "\n");
            bwt.flush();

            String answer;
            while ((answer = br.readLine()) != null) {
                break;
            }

            System.out.println("> " + answer);
        }

        LOG.info("Closing socket");
        socket.close();
        System.out.println("Exiting....");
    }
}
