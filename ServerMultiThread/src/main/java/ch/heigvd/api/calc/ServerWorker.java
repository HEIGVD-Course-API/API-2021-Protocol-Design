package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator worker implementation
 */
public class ServerWorker implements Runnable {

    private final static Logger LOG = Logger.getLogger(Server.class.getName());

    private final static String BIENVENUE = "BIENVENUE";
    private final static String QUITTER = "QUITTER";
    private final static String CALCUL = "CALCUL";
    private final static String ERREUR = "ERREUR";
    private final static String RESULTAT = "RESULTAT";

    private final static int ERREUR_FORMAT = 100;
    private final static int ERREUR_OP_PAS_SUPPORTE = 200;
    private final static int ERREUR_MOT_CLE_INCONNU = 300;
    private final static String MSG_ERREUR_FORMAT = ERREUR + " " + ERREUR_FORMAT + ": MAUVAIS FORMAT\n";
    private final static String MSG_ERREUR_OP_PAS_SUPPORTE = ERREUR + " "  + ERREUR_OP_PAS_SUPPORTE + ": OPERATION PAS SUPPORTEE\n";
    private final static String MSG_ERREUR_MOT_CLE_INCONNU = ERREUR + " "  + ERREUR_MOT_CLE_INCONNU + ": MOT CLE INCONNU\n";

    private final static int NBR_PARAM_MAX = 4;

    private final static String OP_ADD = "+";
    private final static String OP_MULT = "*";

    private final Socket aClientSocket;

    /**
     * Instantiation of a new worker mapped to a socket
     *
     * @param clientSocket connected to worker
     */
    public ServerWorker(Socket clientSocket) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        /* TODO: prepare everything for the ServerWorker to run when the
         *   server calls the ServerWorker.run method.
         *   Don't call the ServerWorker.run method here. It has to be called from the Server.
         */

        aClientSocket = clientSocket;

    }

    /**
     * Run method of the thread.
     */
    @Override
    public void run() {

        /* TODO: implement the handling of a client connection according to the specification.
         *   The server has to do the following:
         *   - initialize the dialog according to the specification (for example send the list
         *     of possible commands)
         *   - In a loop:
         *     - Read a message from the input stream (using BufferedReader.readLine)
         *     - Handle the message
         *     - Send to result to the client
         */
    try{
        BufferedReader reader = null;
        BufferedWriter writer = null;
        String messageBienvenue = BIENVENUE + "\n";

        String messageSpecs = "Bienvenue sur notre serveur de calculs (v1.0) :\n" +
                "Voici les calculs pouvant etre effectue :\n" +
                "Multiplication, format : OP * OP\n" +
                "Addition, format : OP + OP\n";

        LOG.log(Level.INFO, "A client has arrived. We now have a client socket with following attributes:");
        logSocketAddress(aClientSocket);

        LOG.log(Level.INFO, "Getting a Reader and a Writer connected to the client socket...");
        reader = new BufferedReader(new InputStreamReader(aClientSocket.getInputStream(), "UTF-8"));
        writer = new BufferedWriter(new OutputStreamWriter(aClientSocket.getOutputStream(), "UTF-8"));

        LOG.log(Level.INFO, "Sending welcome message and specs of the protocol.");
        writer.write(messageBienvenue);
        writer.write(messageSpecs);
        writer.flush();

        LOG.log(Level.INFO, "Starting my job");
        String request;
        while(!(request = reader.readLine()).equals(QUITTER)) {

            String[] tabParam = new String[NBR_PARAM_MAX];
            final String ESPACE = " ";
            int index;
            int i = 0;

            request = request + " ";
            while((index = request.indexOf(ESPACE)) != -1){
                if(i < NBR_PARAM_MAX){
                    tabParam[i] = request.substring(0,index);
                }
                request = request.substring(index + 1);
                i++;
            }

            LOG.log(Level.INFO, "{0}\n",tabParam[0]);
            LOG.log(Level.INFO, "{0}\n",tabParam[1]);
            LOG.log(Level.INFO, "{0}\n",tabParam[2]);
            LOG.log(Level.INFO, "{0}\n",tabParam[3]);
            LOG.log(Level.INFO, "{0}\n",i);

            if(i == NBR_PARAM_MAX){
                if(!tabParam[0].equals(CALCUL)){
                    writer.write(MSG_ERREUR_MOT_CLE_INCONNU);
                    LOG.log(Level.INFO, MSG_ERREUR_MOT_CLE_INCONNU);
                }else{

                    boolean isOpe1Numeric = true, isOpe2Numeric = true;

                    // faudrait faire une fonction pour que ce soit plus joli
                    for(int j = 0; j < tabParam[1].length(); ++j){
                        if(!Character.isDigit(tabParam[1].charAt(j))){
                            isOpe1Numeric = false;
                        }
                    }

                    for(int j = 0; j < tabParam[3].length(); ++j){
                        if(!Character.isDigit(tabParam[3].charAt(j))){
                            isOpe2Numeric = false;
                        }
                    }

                    if(isOpe1Numeric && isOpe2Numeric){
                        int operande1 = Integer.parseInt(tabParam[1]);
                        int operande2 = Integer.parseInt(tabParam[3]);

                        switch (tabParam[2]){
                            case OP_ADD:
                                LOG.log(Level.INFO, "Doing an addition and sending the result to the client...");
                                writer.write(RESULTAT + "\n");
                                writer.write(operande1 + operande2 + "\n");
                                break;
                            case OP_MULT:
                                LOG.log(Level.INFO, "Doing an multiplication and sending the result to the client...");
                                writer.write(RESULTAT + "\n");
                                writer.write((operande1 * operande2) + "\n");
                                break;
                            default:
                                LOG.log(Level.INFO, MSG_ERREUR_OP_PAS_SUPPORTE);
                                writer.write(MSG_ERREUR_OP_PAS_SUPPORTE);
                                break;
                        }
                    } else{
                        writer.write(MSG_ERREUR_FORMAT);
                    }

                }
            } else {
                writer.write(MSG_ERREUR_FORMAT);
                LOG.log(Level.INFO, MSG_ERREUR_FORMAT);
            }

            writer.flush();

            LOG.log(Level.INFO, "Waiting on client...");
        }

        LOG.log(Level.INFO, "Client ask for quit, closing writer, reader and the client socket...");


        writer.close();
        reader.close();
        aClientSocket.close();
    } catch (IOException e) {
            e.printStackTrace();
    }

    }

    private void logSocketAddress(Socket clientSocket) {
        LOG.log(Level.INFO, "       Local IP address: {0}", new Object[]{clientSocket.getLocalAddress()});
        LOG.log(Level.INFO, "             Local port: {0}", new Object[]{Integer.toString(clientSocket.getLocalPort())});
        LOG.log(Level.INFO, "  Remote Socket address: {0}", new Object[]{clientSocket.getRemoteSocketAddress()});
        LOG.log(Level.INFO, "            Remote port: {0}", new Object[]{Integer.toString(clientSocket.getPort())});
    }
}