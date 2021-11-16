package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator client implementation
 */
public class Client {
    private static final String EOL = "CRLF",
            END_OPE = "- END OPERATIONS",
            BEGIN_OPE = "AVAILABLE OPERATIONS",
            RESULT = "RESULT",
            QUIT = "QUIT";

    // crlf ascii 13 et 10
    private static final Logger LOG = Logger.getLogger(Client.class.getName());

    /**
     * Main function to run client
     *
     * @param args no args required
     */
    public static void main(String[] args) {

        Vector<String> opeList = new Vector<String>();
        Vector<Integer> opeArgsCount = new Vector<Integer>();
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        int PORT = 3101;
        Socket socket = null;
        BufferedReader in = null;
        BufferedWriter out = null;
        BufferedReader stdin = null;

        try {
            socket = new Socket("localhost", PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            stdin = new BufferedReader(new InputStreamReader(System.in));

            boolean waitingOperations = false;
            LOG.log(Level.INFO, "*** Response sent by the server: ***");
            String line = "";
            int c;
            while ((c = in.read()) != -1) {
                line += (char) c;

                // Line complete
                if (line.contains(EOL)) {
                    // remove EOL
                    line = line.replace(EOL, "");

                    // List of operatoins
                    if (line.equals(BEGIN_OPE)) {
                        System.out.println("Available operations");
                        waitingOperations = true;
                        // Clear previous message
                        line = "";
                        // Command registered go to next one
                        continue;
                    }

                    if (line.equals(END_OPE))
                        waitingOperations = false;

                    if (line.contains(RESULT))
                        System.out.println(line);

                    // Adding available operations to the operation list
                    if (waitingOperations) {
                        String[] opeArgs = line.split(" ");
                        opeList.add(opeArgs[0]);
                        opeArgsCount.add(Integer.valueOf(opeArgs[1]));
                        System.out.println(line);
                    } else {
                        String userInput = getUserOpeInput(opeList, opeArgsCount, stdin);
                        if (userInput.equals(QUIT))
                            break;
                        out.write(userInput + EOL);
                        out.flush();
                    }
                    // Clear previous message
                    line = "";
                }
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.toString(), ex);
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.toString(), ex);
            }
            try {
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.toString(), ex);
            }
        }
        /* TODO: Implement the client here, according to your specification
         *   The client has to do the following:
         *   - connect to the server
         *   - initialize the dialog with the server according to your specification
         *   - In a loop:
         *     - read the command from the user on stdin (already created)
         *     - send the command to the server
         *     - read the response line from the server (using BufferedReader.readLine)
         */


    }

    private static String getUserOpeInput(Vector<String> opeList, Vector<Integer> opeArgsCount,  BufferedReader stdin){
        // User input with bufferedReader, so we don't have to create a buffer variable ourselves
        boolean isMalformated = false;
        String userInput = "";
        try {
            do {
                System.out.print("Input operation and operation variables : ");
                userInput = stdin.readLine();

                if (userInput.equals(QUIT))
                    return QUIT;
                // Check user input against available operations
                String[] opeArgs = userInput.split(" ");
                int opeId = opeList.indexOf(opeArgs[0]);
                if (opeId == -1 || opeArgs.length - 1 != opeArgsCount.get(opeId)) {
                    System.out.println("Operation not supported or wrong number of variables...");
                    isMalformated = true;
                } else {
                    for (int i = 1; i < opeArgsCount.get(opeId); ++i) {
                        // Check if variables are numbers
                        if (!isNumeric(opeArgs[i])) {
                            System.out.println("Operation variables must be numeric ...");
                            isMalformated = true;
                            break;
                        }
                    }
                }
            } while (isMalformated);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return userInput;
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
