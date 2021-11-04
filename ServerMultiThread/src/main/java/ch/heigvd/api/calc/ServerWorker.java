package ch.heigvd.api.calc;

import javax.naming.directory.InvalidAttributeValueException;
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
    private final String RESULT = "RESULT IS ";
    private Socket clientSocket;
    private BufferedReader in = null;
    private BufferedWriter out = null;

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
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
        }  catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Run method of the thread.
     */
    @Override
    public void run() {

        try {

            String welcome = "WELCOME \nSYNTAX: REQUEST {OP} {V1} {V2} \n" +
                    "AVAILABLE_OPS \nADD V1 V2 \nMUL V1 V2 \nEND_OF_OPS \n";
            out.write(welcome);
            out.flush();
            System.out.println("Message sent");

            while (!clientSocket.isClosed()) {
                // ... Read and handle message
                String line;
                while ( (line = in.readLine()) != null ) {
                    if (line.equalsIgnoreCase("END")) {
                        break;
                    }
                    String[] parts = line.split(" ");
                    String result = "";
                    switch (parts[0]) {
                        case "ADD":
                            try {
                                result = add(Integer.parseInt(parts[1]),
                                        Integer.parseInt(parts[2]));
                            } catch (InvalidAttributeValueException e) {
                                LOG.log(Level.SEVERE, e.toString(), e);
                            }
                            break;
                        case "MUL":
                            try {
                                result = multiply(Integer.parseInt(parts[1]),
                                        Integer.parseInt(parts[2]));
                            } catch (InvalidAttributeValueException e) {
                                LOG.log(Level.SEVERE, e.toString(), e);
                            }
                            break;
                    }
                    out.write(result);
                    out.flush();
                }
                clientSocket.close();
                in.close();
                out.close();
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.toString(), e);
        }


    }


    private String add(int v1, int v2) throws InvalidAttributeValueException {
        return RESULT + (v1 + v2) + '\n';
    }

    private String multiply(int v1, int v2) throws InvalidAttributeValueException {
        return RESULT + (v1 * v2) + '\n';
    }
}