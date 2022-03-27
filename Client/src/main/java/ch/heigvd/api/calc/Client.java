package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
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
        /* Check args have been given */
        if (args.length != 2){
            System.out.println("Invalid number of arguments specified.");
            return;
        }

        LOG.info(Arrays.toString(args));

        String serverAddress = args[0];
        int serverPort = Integer.valueOf(args[1]);

        PrintWriter socketOutputStream = null;
        InputStream socketInputStream = null;
        String welcome, command, result;

        try {
            Socket socket = new Socket(serverAddress, serverPort);
            socketInputStream = socket.getInputStream();
            socketOutputStream = new PrintWriter(socket.getOutputStream());
            stdin = new BufferedReader(new InputStreamReader(System.in));

            BufferedReader socketReader = new BufferedReader(new InputStreamReader(socketInputStream));

            // Read welcome message
            welcome = socketReader.readLine();
            System.out.println(welcome);


            while(socket.isConnected()){
                // Read command prompt
                command = socketReader.readLine();
                if(command == null)
                    return;
                LOG.info(command);

                // Read input
                String input = stdin.readLine();

                // Send user input to server
                socketOutputStream.println(input);
                socketOutputStream.flush();
                if(input.toUpperCase().strip().equals("QUIT"))
                    return;
                // Read response from server
                result = socketReader.readLine();
                LOG.info(result);
            }
        } catch (UnknownHostException e){
            System.out.println("Cannot establish connection to host " + serverAddress + " on port " + serverPort);
            return;
        } catch (IOException e){
            System.out.println(e);
            return;
        }



    }
}
