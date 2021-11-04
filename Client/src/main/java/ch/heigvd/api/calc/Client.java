package ch.heigvd.api.calc;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator client implementation
 */
public class Client {

    private static final Logger LOG = Logger.getLogger(Client.class.getName());
    private static final int PORT = 4242;
    private static final String HOST = "localhost";

    /**
     * Main function to run client
     *
     * @param args no args required
     */
    public static void main(String[] args) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        Socket clientSocket = null;
        BufferedWriter out = null;
        BufferedReader in = null;
        BufferedReader usrin = new BufferedReader(new InputStreamReader(System.in));



        try {
            clientSocket = new Socket(HOST, PORT);
            System.out.println("Connected to \"" + clientSocket.getInetAddress() + "\"");
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String line = "", response;

            while ((response = in.readLine()) != null) {

                // The server greets us and lists its supported ops
                if (response.startsWith("WELCOME"))
                    while (!response.startsWith("END_OF_OPS")) {
                        System.out.println(response);
                        response = in.readLine();
                    }

                System.out.println(response);

                if (line.startsWith("END"))
                    break;

                System.out.print("> ");
                line = usrin.readLine();

                out.write(line + '\n');
                out.flush();
            }

        } catch (IOException e){
            LOG.log(Level.SEVERE, e.toString(), e);
        } finally {
            try {
                if(in != null) in.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, e.toString(), e);
            }
            try {
                if(out != null) out.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, e.toString(), e);
            }
            try {
                if(clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, e.toString(), e);
            }
        }
    }
}