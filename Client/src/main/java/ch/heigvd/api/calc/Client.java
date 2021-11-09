package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator client implementation
 */
public class Client {

    private static final Logger LOG = Logger.getLogger(Client.class.getName());

    private static final String host = "localhost";
    private static final int port = 11111;

    private static Socket socket;
    private static BufferedReader stdin = null;
    private static BufferedReader in = null;
    private static BufferedWriter out = null;

    /**
     * Main function to run client
     *
     * @param args no args required
     */
    public static void main(String[] args) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        try {
            try {
                socket = new Socket(host, port);
            } catch (SocketException e) {
                System.out.printf("Can't connect to server '%s' on port %d\n", host, port);
                System.exit(1);
            }

            stdin = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            System.out.printf("Connected to server '%s' on port %d\n", host, port);
            System.out.println(in.readLine());

            String q = "";
            String r = "";
            while (true) {
                System.out.print("$: ");
                q = stdin.readLine();
                if (!q.equals("")) {
                    q = q.toLowerCase(Locale.ROOT);
                    out.write(q + "\r\n");
                    out.flush();

                    r = in.readLine();
                    System.out.println(r);

                    // We may have requested the end of the connection with 'exit'
                    if (q.equals("exit"))
                        break;
                }
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
        } finally {

            // Closing the connections properly
            try {
                if (!socket.isClosed())
                    socket.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, null, e);
            }
            try {
                stdin.close();
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
