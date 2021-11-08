package ch.heigvd.api.calc;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.Socket;

/**
 * Calculator client implementation
 */
public class Client {

    protected static final Logger LOG = Logger.getLogger(Client.class.getName());

    // Fields
    private String serverHost;
    private Socket clientSocket;
    private BufferedWriter out;
    private BufferedReader in;

    public boolean connect(String serverHost, int port) {
        if (serverHost.isEmpty())
            return false;

        this.serverHost = serverHost;
        try {
            // Initializing a new connection
            clientSocket = new Socket(serverHost, port);
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            return true;
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.toString(), ex);
            close();
        }
        return false;
    }
    public boolean send(String request) {
        if (ready()) {
            try {
                out.write(request);
                out.flush();
                return true;
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.toString(), ex);
            }
        }
        return false;
    }
    public String receive() {
        if (ready()) {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    LOG.log(Level.INFO, line);
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.toString(), ex);
            }
        }
        return null;
    }
    public boolean ready() {
        return (clientSocket != null && out != null && in != null && !clientSocket.isClosed());
    }
    public void close(){
        try {
            if (out != null) out.close();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.toString(), ex);
        }
        try {
            if (in != null) in.close();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.toString(), ex);
        }
        try {
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.toString(), ex);
        }
    }
    /**
     * Main function to run client
     *
     * @param args no args required
     */
    public static void main(String[] args) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        BufferedReader stdin = null;

        /* TODO: Implement the client here, according to your specification
         *   The client has to do the following:
         *   - connect to the server
         *   - initialize the dialog with the server according to your specification
         *   - In a loop:
         *     - read the command from the user on stdin (already created)
         *     - send the command to the server
         *     - read the response line from the server (using BufferedReader.readLine)
         */

        ACI_Client clt = new ACI_Client();
        if(clt.connect("localhost", 7548)){
            System.out.println(clt.operation(ACI_Client.operation.ADD, 1, 1, 3));
            clt.close();
            //System.out.println(clt.receive());
        }

    }
}
