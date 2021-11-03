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
    private static final String BIENVENUE = "BIENVENUE";
    private static final String CALCUL = "CALCUL";
    private static final String RESLUTAT = "RESULT";
    private static final String ERREUR = "ERREUR";
    private static final String FIN = "QUITTER";
    private static final String IP = "192.168.43.22";
    private static final int PORT = 1024;
    private Socket sClient;



    public Boolean initialiseConnection(){
        Socket clientSocket = null;
        BufferedWriter out = null;
        BufferedReader in = null;

        try {
            clientSocket = new Socket(IP, PORT);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


            String line;
            Boolean hasBienvenue = false;
            while ((line = in.readLine()) != null){
                if(line.equals(BIENVENUE)){
                    sClient = clientSocket;
                    hasBienvenue = true;
                    System.out.println("Connection avec le serveur initialisé");
                } else {
                    System.out.println(line);
                }
            }
            if(!hasBienvenue){
                System.out.println("Connection avec le serveur impossible");
            }
            return hasBienvenue;
        } catch (IOException ex) {
            System.out.println("Connection avec le serveur impossible");
            return false;
        }
    }

    public String sendRequest(String request){
        BufferedWriter out = null;
        BufferedReader in = null;
        try{
            LOG.log(Level.INFO, request);
            out = new BufferedWriter(new OutputStreamWriter(sClient.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(sClient.getInputStream()));
            out.write(request);
            out.flush();
            String line;
            while((line = in.readLine()) != null);
            return line;
        } catch (IOException ex) {
            LOG.log(Level.INFO, "SA MARCHE PO");
        }
        return "";
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

        stdin = new BufferedReader(new InputStreamReader(System.in));

        Client client = new Client();

        Boolean stop = false;
        if(client.initialiseConnection()){
            while(!stop){
                try {
                    String request = stdin.readLine();
                    if(request.equals(FIN)) stop = true;
                    System.out.println(client.sendRequest(request));
                } catch(IOException ex){

                }
            }
        }

        //client.sendRequest("CALCUL 3 + 2");

    }
}
