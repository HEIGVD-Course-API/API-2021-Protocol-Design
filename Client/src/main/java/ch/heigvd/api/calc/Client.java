package ch.heigvd.api.calc;

import com.sun.source.tree.Scope;

import javax.xml.stream.events.StartDocument;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator client implementation
 */
public class Client {
    private static final int BUFFER_SIZE = 100;


    private static final Logger LOG = Logger.getLogger(Client.class.getName());
    static final String HOST = "localhost";
    static final int PORT = 9999;

    /**
     * Main function to run client
     *
     * @param args no args required
     */
    public static void main(String[] args) {

        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        BufferedReader stdin = null; // Read from the standard input
        BufferedReader in = null; // Read from the Server
        PrintWriter out = null; // Write to the Server
        Socket clientSocket = null;

        try {
            clientSocket = new Socket(HOST, PORT); // Connect to the server
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
            PrintWriter stdout = new PrintWriter(new OutputStreamWriter(System.out));

            // Read info from server
            stdout.println(in.readLine());
            stdout.flush();

            stdin = new BufferedReader(new InputStreamReader(System.in));
            String clientInput = "";
            while (!clientInput.equals("QUIT")) {
                // Write request
                clientInput = stdin.readLine();
                out.println(clientInput);
                out.flush();
                // Read result and print it
                stdout.println(in.readLine());
                stdout.flush();
            }


        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            try {
                if(in != null) in.close();
                if(out != null) out.close();
                if(clientSocket != null) clientSocket.close();
            } catch(IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
