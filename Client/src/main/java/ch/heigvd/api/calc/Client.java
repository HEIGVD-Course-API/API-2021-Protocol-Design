package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;




/**
 * Calculator client implementation
 */
public class Client {

    private static final Logger LOG = Logger.getLogger(Client.class.getName());

    private static Socket clientSocket = null;
    private static BufferedReader reader = null;
    private static BufferedWriter writer = null;
    private static boolean running = true;

    // Configuration values that depend on server-side formatting
    private static final String serverLastGreetingWords = "Current value : 0.0";
    private static final String serverPrompt = "> ";
    private static final String errorStrId = "ERR:";
    private static final String endMsg = "Last value";


    /**
     * Main function to run client
     *
     * @param args no args required
     */
    public static void main(String[] args) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        if (!connectToServer("localhost", 9999)){
            System.out.println("Can't connect to server.\n");
            System.out.println("Exiting...");
            System.exit(0);
        }

        String line;
        while (running) {
            try {
                line = stdin.readLine();

                if (line.isEmpty())
                    continue;

                String res = sendMsg(line);
                ArrayList<String> parsedRes = new ArrayList<>();
                var status = parseResponse(res, parsedRes);

                switch (status) {
                    case OK:
                        System.out.println(parsedRes.get(1));
                        break;
                    case SERVER_ERROR:
                        System.out.println("Bad server-side formatting.");
                        break;
                    case BYE:
                        // Read the last message from the server.
                        System.out.println(reader.readLine());
                        running = false;
                        break;
                    default:
                        System.out.println("The server responded with an error : " + parsedRes.get(1));

                }

            } catch (IOException ex) {
                running = false;
                LOG.log(Level.SEVERE, null, ex);
            }
        }


        if (closeConnection()) {
            System.out.println("Connection properly closed.");
            System.exit(0);
        } else {
            System.out.println("Error occurred while closing connection.");
            System.exit(1);
        }

    }

    private static boolean connectToServer(String host, int listenPort) {
        try {
            clientSocket = new Socket(host, listenPort);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Can't open connection");
            return false;
        }
        try {
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Can't open output stream.");
            return false;
        }
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            String line;
            boolean msgEnd = false;
            while (!msgEnd) {
                line = reader.readLine();
                if (line == null || line.equals(serverLastGreetingWords))
                    msgEnd = true;
                System.out.println(line);
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Can't open input stream.");
            try {
                writer.close();
            } catch (IOException e2) {
                LOG.log(Level.SEVERE, "IO streams exception.", e2);
                System.exit(1);
            }
        }
        return true;
    }

    private static boolean closeConnection() {
        boolean thrown = false;
        try {
            if (writer != null) writer.close();
        } catch (IOException ex) {
            thrown = true;
            LOG.log(Level.SEVERE, ex.toString(), ex);
        }
        try {
            if (reader != null) reader.close();
        } catch (IOException ex) {
            thrown = true;
            LOG.log(Level.SEVERE, ex.toString(), ex);
        }
        try {
            if (clientSocket != null && ! clientSocket.isClosed()) clientSocket.close();
        } catch (IOException ex) {
            thrown = true;
            LOG.log(Level.SEVERE, ex.toString(), ex);
        }
        return !thrown;
    }

    /**
     * Send a message to server.
     *
     * @param message the message to send to server
     * @return response from server or empty string if failed.
     */
    private static String sendMsg(String message) {
        var response = "";
        try {
            writer.write(message + "\n");
            writer.flush();
            response = reader.readLine();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Sending message to server failed.");
        }
        return response;
    }

    private static ErrorCode parseResponse(String rawResponse, ArrayList<String> parsedResponse) {

        if(rawResponse.indexOf(serverPrompt) != 0)
            return ErrorCode.SERVER_ERROR;
        else
            parsedResponse.add(serverPrompt);

        var subStr = rawResponse.substring(serverPrompt.length());

        var resTable = rawResponse.split(" ");

        if (resTable.length == 2) {
            try {
                double d = Double.parseDouble(resTable[1]);
                parsedResponse.add(Double.toString(d));
                return ErrorCode.OK;
            } catch (NumberFormatException e) {
                return ErrorCode.SERVER_ERROR;
            }
        }

        if(subStr.contains(errorStrId)) {
            parsedResponse.add(rawResponse.substring(rawResponse.indexOf(resTable[3])));
            return ErrorCode.valueOf(resTable[2]);
        }

        if(subStr.contains(endMsg)) {
            parsedResponse.add(subStr);
            return ErrorCode.BYE;
        }

        parsedResponse.add(subStr);
        return ErrorCode.INVALID_INPUT;
    }
}
