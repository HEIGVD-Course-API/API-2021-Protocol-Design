package ch.heigvd.api.calc;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator client implementation
 */
public class Client {

    private static final Logger LOG = Logger.getLogger(Client.class.getName());
    public static  final int PORT = 1339;
    public static  final String SERVER = "localhost";

    /**
     *
     */
    public static void main(String[] args) {

        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        BufferedReader stdin = null;

        stdin = new BufferedReader(new InputStreamReader(System.in));

        Socket clientSocket = null;
        BufferedWriter out = null;
        BufferedReader in = null;

        try {
            clientSocket = new Socket(SERVER, PORT);
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            in  = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String line = "";
            line = in.readLine();
            LOG.log(Level.INFO, line);

            while((line = stdin.readLine()) != null){
                if(line.equalsIgnoreCase("quit"))
                    break;

                out.write(line+"\r\n");
               // out.flush();

                String answer = in.readLine();
                LOG.log(Level.INFO, "Result: " + answer);
                line = in.readLine();
                LOG.log(Level.INFO, line);
            }
            LOG.log(Level.INFO, "See you!");
            clientSocket.close();
        }
        catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.toString(), ex);
        } finally {
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
                if (clientSocket != null && ! clientSocket.isClosed()) clientSocket.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.toString(), ex);
            }
        }
    }
}
