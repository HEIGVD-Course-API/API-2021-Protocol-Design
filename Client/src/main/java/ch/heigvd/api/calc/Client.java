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
    public static final int PORT = 1339;
    public static final String SERVER = "localhost";

    public static void getMsgFromServer(BufferedReader in) throws IOException {
        int cnt = 0;
        String msgFromServ;
        while (((msgFromServ = in.readLine()) != null) && (!(msgFromServ).equals(""))) {
            ++cnt;
            System.out.println(msgFromServ);
        }
        if (cnt == 0) {
            System.out.println("No answer From Server ! \n");
        }
    }

    public static Socket connect(String srv, int port) throws IOException {
        Socket clientSocket = null;
        clientSocket = new Socket(SERVER, PORT);
        return clientSocket;
    }

    public static void sendRequest(String request, BufferedWriter out) throws IOException {
        //send request
        out.write(request + "\r\n");
        out.flush();

    }


    /**
     *
     */
    public static void main(String[] args) {

        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");


        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        Socket clientSocket = null;
        BufferedWriter out = null;
        BufferedReader in = null;

        try {

            //Connect to the server
            clientSocket = connect(SERVER, PORT);

            //Create pipes
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //display Welcome MSG
            getMsgFromServer(in);

            String userRequest;

            while (true) {

                //get userInput
                userRequest = stdin.readLine();

                //condition to stop client
                if (userRequest.equalsIgnoreCase("quit"))
                    break;

                //send request to serv
                sendRequest(userRequest, out);

                //get response
                getMsgFromServer(in);

            }
            clientSocket.close();
        } catch (IOException ex) {
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
                if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.toString(), ex);
            }
        }
    }
}
