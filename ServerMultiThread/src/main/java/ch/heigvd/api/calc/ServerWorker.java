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

    private final static Logger LOG = Logger.getLogger(ServerWorker.class.getName());

    private Socket clientSocket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;

    /**
     * Instantiation of a new worker mapped to a socket
     *
     * @param clientSocket connected to worker
     */
    public ServerWorker(Socket clientSocket) {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        try {
            this.clientSocket = clientSocket;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Run method of the thread.
     */
    @Override
    public void run() {
        try
        {
            String line;
            while (!clientSocket.isClosed()) {
                while ((line = in.readLine()) != null) {
                    // reads until client close connexion
                    String[] s = line.split(" ");
                    if (s.length < 3) {
                        if (s[0].equalsIgnoreCase("bye"))
                        {
                            out.write("Goodbye !\n");
                            clientSocket.close();
                            break;
                        }
                        sendErrorCode();
                        continue;
                    }
                    float a;
                    float b;
                    float answer;

                    try
                    {
                        a = Float.parseFloat(s[1]);
                        b = Float.parseFloat(s[2]);
                    }
                    catch (NumberFormatException e)
                    {
                        sendErrorCode();
                        continue;
                    }


                    switch (s[0]) {
                        case "add":
                            answer = a + b;
                            break;
                        case "sub":
                            answer = a - b;
                            break;
                        case "mul":
                            answer = a * b;
                            break;
                        default:
                            sendErrorCode();
                            continue;
                    }
                    sendAnswer(answer);
                }
            }
        } catch (IOException e){
            System.err.println("Client connexion error");
            System.err.println(e.getMessage());
        }
    }

    private void sendErrorCode()
    {
        out.write(Code.ERROR.toString());
    }

    private void sendAnswer(float answer){
        String string = Code.OK + " " + answer;
        out.write(string);
    }

    private enum Code {
        OK(0),
        ERROR(1);
        private final int value;

        Code(int val){
            value = val;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
}