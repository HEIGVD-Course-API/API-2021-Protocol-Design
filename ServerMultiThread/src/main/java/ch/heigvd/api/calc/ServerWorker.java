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

    private Socket clientSocket;
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

        /* TODO: prepare everything for the ServerWorker to run when the
         *   server calls the ServerWorker.run method.
         *   Don't call the ServerWorker.run method here. It has to be called from the Server.
         */
        try {
            this.clientSocket = clientSocket;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public static boolean isNumeric(String string) {
        int intValue;

        System.out.printf("Parsing string: \"%s\"%n", string);

        if(string == null || string.equals("")) {
            System.out.println("String cannot be parsed, it is null or empty.");
            return false;
        }

        try {
            intValue = Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Input String cannot be parsed to Integer.");
        }
        return false;
    }

    private String showOperation(String line) {
        String[] args = line.split(" ");
        if (args.length == 3) {
            if (isNumeric(args[1]) && isNumeric(args[2])) {
                double x = Double.parseDouble(args[1]);
                double y = Double.parseDouble(args[2]);
                if (args[0].equalsIgnoreCase("ADD")){
                    return String.format("> %.1f", x + y);
                } else if (args[0].equalsIgnoreCase("SUB")) {
                    return String.format("> %.1f", x - y);
                } else if (args[0].equalsIgnoreCase("MUL")) {
                    return String.format("> %.1f", x * y);
                } else if (args[0].equalsIgnoreCase("DIV")) {
                    if (y != 0)
                        return String.format("> %.1f", x / y);
                    return "> Cannot divide by 0.";
                }
            }

        }


        return "> Error: invalid command.";
    }

    private String showHelp() {
        return "> Existing commands:\n" +
                "HELP\t: Show all the commands & operations. Provides examples.\n" +
                "COMPUTE [OPERATION] [OPERAND1] [OPERAND2] : Execute the operation given as argument.\n" +
                "BYE\t: Terminate the connection.\n\n" +
                "Exisiting operations:\n" +
                "ADD (addition)\n" +
                "SUB (substraction)\n" +
                "MUL (mutiplication)\n" +
                "DIV (division)\n\n" +
                "Example: to execute an addition, type: COMPUTE ADD 1 2.";
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

        String line;

        out.println("> Welcome, you are now connected with our calculation server.\nType HELP to see example of commands.");
        out.flush();
        try {
            LOG.info("Reading until client sends BYE or closes the connection...");
            while ((line = in.readLine()) != null) {
                if (line.equalsIgnoreCase("help")) {
                    out.println(showHelp());
                    out.flush();
                } else if (line.equalsIgnoreCase("bye")) {
                    out.println("> Good bye!");
                    out.flush();
                    break;
                } else {
                    System.out.println("From client: " + line);
                    out.println(showOperation(line));
                    out.flush();
                }

                out.println("> END TRANSMISSION");
                out.flush();
            }

            LOG.info("Cleaning up resources...");
            clientSocket.close();
            in.close();
            out.close();

        } catch (IOException ex) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
            if (out != null) {
                out.close();
            }
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}