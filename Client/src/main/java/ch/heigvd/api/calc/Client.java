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
    public static void main(String[] args) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        /* TODO: Implement the client here, according to your specification
         *   The client has to do the following:
         *   - connect to the server
         *   - initialize the dialog with the server according to your specification
         *   - In a loop:
         *     - read the command from the user on stdin (already created)
         *     - send the command to the server
         *     - read the response line from the server (using BufferedReader.readLine)
         */

        Socket clientSocket = null;
        BufferedWriter clientOut;
        BufferedReader clientIn;
        BufferedReader stdin;
        try{
            clientSocket = new Socket("127.0.0.1", 3003);
            clientOut = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            stdin = new BufferedReader(new InputStreamReader(System.in));

        }catch (Exception e){
            LOG.log(Level.SEVERE, "Error while creating socket", e);
            return;
        }

        String input = "";
        while (true){

            try {
                String serverAnswer = clientIn.readLine();
                String[] s = serverAnswer.split(" ");
                String code = s[0];

                switch (code){
                    case "Welcome":
                        System.out.println(serverAnswer);
                        break;
                    case "1" :
                        System.out.println("Wrong command !");
                        break;
                    case "0" :
                        System.out.println("Answer : " + s[1]);
                        break;
                    case "Goodbye":
                        System.out.println(serverAnswer);
                        clientSocket.close();
                        return;
                    default:
                        System.out.println("Wrong server answer");
                        break;
                }

            }
            catch (IOException e){
                LOG.log(Level.SEVERE, "Error while reading server answer", e);
            }

            try {
                input = stdin.readLine().toLowerCase();
            }catch (Exception e){
                LOG.log(Level.SEVERE, "Error while reading user input", e);
                return;
            }

            try {
                clientOut.write(input + '\n');
                clientOut.flush();
            }
            catch (IOException e){
                LOG.log(Level.SEVERE, "Error while writing user input", e);
            }


        }
    }
}
