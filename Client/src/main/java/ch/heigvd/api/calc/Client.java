package ch.heigvd.api.calc;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;
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
    public static void main(String[] args) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");
        Socket clientSocket = null;
        BufferedReader in = null;
        BufferedWriter out = null;
        BufferedReader stdin = null;

        boolean serverCanListen = false;
        boolean shouldQuit = false;
        String serverInput = "";
        String userInput = "";
        
        final String headerEnd = "END_OF_OPERATIONS";
        final int port = 4269;
        final String host = "10.192.94.57";

        try {
            clientSocket = new Socket(host, port);
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(),
                    StandardCharsets.UTF_8));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            stdin = new BufferedReader(new InputStreamReader(System.in));
            while (!shouldQuit && (serverInput = in.readLine()) != null) {
                LOG.log(Level.INFO, serverInput);
                if (serverInput.equals(headerEnd))
                    serverCanListen = true;

                if (serverCanListen) {
                    userInput = stdin.readLine();
                    if (userInput.equals("QUIT")) {
                        shouldQuit = true;
                    } else {
                        out.write(userInput + "\n");
                        out.flush();
                    }
                }
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Global error");
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "out BufferedReader cannot be closed");
            }
            try {
                if (in != null) in.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "in BufferedReader cannot be closed");
            }
            try {
                if (stdin != null) stdin.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "stdin BufferedReader cannot be closed");
            }
            try {
                if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "clientSocket cannot be closed");
            }
        }
    }
}
