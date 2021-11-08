package ch.heigvd.api.calc;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Level;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Calculator client implementation
 */
public class Client {

    private static final Logger LOG = Logger.getLogger(Client.class.getName());
    private final static int PORT = 42000;
    private final static String IP = "127.0.0.1";

    /**
     * Main function to run client
     *
     * @param args no args required
     */
    public static void main(String[] args) throws IOException {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        final Socket CLIENT_SOCKET;
        
        CLIENT_SOCKET = new Socket(IP,PORT);
        LOG.log(Level.INFO, "Creating a client socket and binding it on any of the available network interfaces and on port {0}",
                new Object[]{Integer.toString(PORT)});

        logSocketAddress(CLIENT_SOCKET);

        // Output du socket pour envoyer les commandes
        OutputStream output = CLIENT_SOCKET.getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);

        // Flux pour recevoir les infos du serveur
        InputStream input = CLIENT_SOCKET.getInputStream();
        BufferedReader stdin = new BufferedReader(new InputStreamReader(input));

        String msg = "Server info : ";
        System.out.println(msg);
        while (!Objects.equals(msg = stdin.readLine(), "end")) {
            System.out.println(msg);
        }

        //Compute
        Scanner sc = new Scanner(System.in);

        while(true) {
            // Nouvelle commande de l'utilisateur
            String command = sc.nextLine().trim();

            if (command.equalsIgnoreCase("bye")) {
                writer.println("bye");
                break;
            }
            // Utilsateur écrit sa commande
            String[] tokens = command.split(" ");

            // Une commande a une longueur minimal de 2 (factorial 1)
            if (tokens.length < 2){
                System.out.println("Error: Use format <operator> op1 [op2]");
                continue;
            }

            // Checker les nombres donnés
            ArrayList<Integer> numbers = new ArrayList<>(tokens.length - 1);
            try {
                for (int i = 1; i < tokens.length; ++i) {
                    numbers.add(Integer.parseInt(tokens[i]));
                }
            }
            catch (NumberFormatException e){
                System.out.println("Error: Please use number in numerical format.");
                continue;
            }

            // Construction du message
            StringBuilder toSend = new StringBuilder(tokens[0]).append(" ");
            for (Integer i : numbers) {
                toSend.append(i).append(" ");
            }

            // Envoie au serveur la commande
            writer.println(toSend);

            // Le serveur répond
            msg = stdin.readLine();
            while(msg.isEmpty()){
                msg = stdin.readLine();
            }
            System.out.println(msg);


        }

        stdin.close();
        writer.close();
        CLIENT_SOCKET.close();

    }

    private static void logSocketAddress(Socket clientSocket) {
        LOG.log(Level.INFO, "       Local IP address: {0}", new Object[]{clientSocket.getLocalAddress()});
        LOG.log(Level.INFO, "             Local port: {0}", new Object[]{Integer.toString(clientSocket.getLocalPort())});
        LOG.log(Level.INFO, "  Remote Socket address: {0}", new Object[]{clientSocket.getRemoteSocketAddress()});
        LOG.log(Level.INFO, "            Remote port: {0}", new Object[]{Integer.toString(clientSocket.getPort())});
    }
}
