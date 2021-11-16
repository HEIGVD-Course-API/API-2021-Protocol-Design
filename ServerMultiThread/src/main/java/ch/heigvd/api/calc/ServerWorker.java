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

    private static final String EOL = "CRLF",
            END_OPE = "- END OPERATIONS",
            BEGIN_OPE = "AVAILABLE OPERATIONS",
            QUIT = "QUIT";
    private  static final String[] opeList = {"ADD", "SUB", "MULTIPLY"};
    private  static final int[] opeArgsCount = {2,2,2};

    private BufferedReader in = null;
    private BufferedWriter out = null;
    private final static Logger LOG = Logger.getLogger(ServerWorker.class.getName());

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
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));

        }catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage());
        }
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

        try {
            out.write(BEGIN_OPE + EOL);
            out.flush();
            for (int i = 0; i < opeList.length; ++i) {
                out.write(opeList[i] + " " + opeArgsCount[i] + EOL);
                out.flush();
            }

            out.write(END_OPE + EOL);
            out.flush();

            String line = "";
            LOG.log(Level.INFO, "Connected with client and sent operation list");

            int c;
            while ((c = in.read()) != -1) {
                line += (char) c;
                if (line.contains(EOL)) {
                    LOG.log(Level.INFO, "received : " + line);
                    // remove EOL
                    line = line.replace(EOL, "");
                    if (line.equals(QUIT))
                        break;

                    // Check user input against available operations
                    String[] opeArgs = line.split(" ");
                    int opeId = getOpId(opeArgs[0]);
                    if (opeId == -1 || opeArgs.length - 1 != opeArgsCount[opeId]) {
                        System.out.println(" Error : Operation not supported or wrong number of variables...");
                    } else {
                        for (int i = 1; i < opeArgsCount[opeId]; ++i) {
                            // Check if variables are numbers
                            if (!isNumeric(opeArgs[i])) {
                                System.out.println("Error : Operation variables must be numeric ...");
                                break;
                            }
                        }
                        int result = 0;
                        switch (opeId) {
                            case 0:
                                result = Integer.parseInt(opeArgs[1]) + Integer.parseInt(opeArgs[2]);
                                break;
                            case 1:
                                result = Integer.parseInt(opeArgs[1]) - Integer.parseInt(opeArgs[2]);
                                break;
                            case 2:
                                result = Integer.parseInt(opeArgs[1]) * Integer.parseInt(opeArgs[2]);
                                break;
                        }
                        out.write("RESULT : " + result + EOL);
                        out.flush();
                    }

                    if (line.contains(QUIT)) {
                        break;
                    }
                    line = "";
                }
            }
        }catch (Exception e) {
            LOG.log(Level.SEVERE, e.getLocalizedMessage() + "l134");
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, e.getMessage());
            }
            try {
                if (out != null) out.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, e.getMessage());
            }
        }
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private static int getOpId(String ope) {
        for (int i = 0; i < opeList.length; ++i) {
            if (opeList[i].equals(ope))
                return i;
        }
        return -1;
    }
}
