package ch.heigvd.api.calc;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.Socket;

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
        final int SERVER_PORT = 4269;
        final String SERVER_HOST= "127.0.0.1";

        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        Socket clientSocket = null;
        BufferedReader stdin = null;
        BufferedReader serverResponse = null;
        BufferedWriter serverRequest = null;

        /* TODO: Implement the client here, according to your specification
         *   The client has to do the following:
         *   - connect to the server
         *   - initialize the dialog with the server according to your specification
         *   - In a loop:
         *     - read the command from the user on stdin (already created)
         *     - send the command to the server
         *     - read the response line from the server (using BufferedReader.readLine)
         */

        try{
            // Initialise la connection avec le serveur
            clientSocket = new Socket(SERVER_HOST, SERVER_PORT);

            // Récupérateur de l'entrée utilisateur
            stdin = new BufferedReader(new InputStreamReader(System.in));

            // Récupérateur des réponses du serveur
            serverResponse = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Envoyeur de requêtes au serveur
            serverRequest = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            boolean connectionClosed = false;

            do{
                String request = stdin.readLine() + "\r\n";
                serverRequest.write(request);
                serverRequest.flush();

                String response = serverResponse.readLine();
                switch(response.substring(0, 3)){
                    case "EXS":
                        connectionClosed = true;
                    case "RES":
                        System.out.println("Le résultat de votre opération est : " + response.substring(4));
                        break;
                    case "CAE":
                        System.out.println("Le calcul est erroné.");
                        break;
                    case "COE":
                        System.out.println("La commande est erronée.");
                        break;
                }
            }while(!connectionClosed);
        }
        catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.toString(), ex);
        } finally {
            try {
                if (serverRequest != null) serverRequest.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.toString(), ex);
            }
            try {
                if (serverResponse != null) serverResponse.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.toString(), ex);
            }
            try {
                if (stdin != null) stdin.close();
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
