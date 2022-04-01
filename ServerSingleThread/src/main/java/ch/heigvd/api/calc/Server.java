package ch.heigvd.api.calc;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator server implementation - single threaded
 */
public class Server {

    private final static Logger LOG = Logger.getLogger(Server.class.getName());

    /**
     * Main function to start the server
     */
    public static void main(String[] args) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        (new Server()).start();
    }

    /**
     * Start the server on a listening socket.
     */
    private void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(42069);

            Socket client = serverSocket.accept();
            this.handleClient(client);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle a single client connection: receive commands and send back the result.
     *
     * @param clientSocket with the connection with the individual client.
     */
    private void handleClient(Socket clientSocket) {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()
                    )
            );

            BufferedWriter bwt = new BufferedWriter(
                    new OutputStreamWriter(
                            clientSocket.getOutputStream()
                    )
            );

            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals("GOODBYE")) {
                    break;
                }
                bwt.write(this.calculate(line));
                bwt.flush();
            }
            bwt.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String calculate(String line) {
        String[] vals = line.split(" ");
        switch (vals[0]) {
            case "ADD":
                return String.valueOf(Double.parseDouble(vals[1]) + Double.parseDouble(vals[2]));
            case "SUB":
                return String.valueOf(Double.parseDouble(vals[1]) - Double.parseDouble(vals[2]));
            case "DIV":
                return String.valueOf(Double.parseDouble(vals[1]) / Double.parseDouble(vals[2]));
            case "MOD":
                return String.valueOf(Double.parseDouble(vals[1]) % Double.parseDouble(vals[2]));
            case "EXP":
                return String.valueOf(Math.pow(Double.parseDouble(vals[1]), Double.parseDouble(vals[2])));
        }

        return "You F****** MORON";
    }
}