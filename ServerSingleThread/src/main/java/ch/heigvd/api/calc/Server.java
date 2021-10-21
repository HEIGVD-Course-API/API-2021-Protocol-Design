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

    private final static Logger LOG = Logger.getLogger(Server.class.getName());
    private final int PORT = 4242;
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
        Socket clientSocket = null;
        BufferedWriter stdout = null;
        BufferedReader stdin = null;

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println(serverSocket.getLocalSocketAddress());
        } catch (IOException e){
            LOG.log(Level.SEVERE, null, e);
            return;
        }

        while(true) {
            try{
                LOG.log(Level.INFO, "waiting for new client");
                clientSocket = serverSocket.accept();
                if(clientSocket.isClosed()) {
                    System.out.println("client socket is closed");
                }

                stdin = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream(),
                                StandardCharsets.UTF_8));
                stdout =
                        new BufferedWriter(
                                new OutputStreamWriter(
                                        clientSocket.getOutputStream(),
                                        StandardCharsets.UTF_8));

                //String welcome = new String("WELCOME\n");
                //stdout.write(welcome, 0, welcome.length());
                //stdout.flush();
                stdout.write('W');
                stdout.flush();
                System.out.println("Message sent");


                LOG.info("Cleaning up resources...");
                clientSocket.close();
                stdin.close();
                stdout.close();

                //handleClient(clientSocket);
            } catch (IOException e) {
                LOG.log(Level.SEVERE, e.toString(), e);
            } finally { // Is finally necessary ? Everything is in catch in the
                // example
                try {
                    if(clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, e.toString(), e);
                }
                try {
                    if(stdin != null) stdin.close();
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, e.toString(), e);
                }
                try {
                    if(stdout != null) stdout.close();
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, e.toString(), e);
                }
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





    }
}