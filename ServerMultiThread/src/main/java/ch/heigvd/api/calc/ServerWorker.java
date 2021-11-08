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
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        try {
            this.clientSocket = clientSocket;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Helper to check is given string is a number.
     * @param string string to test
     * @return true if string can be parsed
     */
    private static boolean isNumeric(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    /**
     * Do an operation given by the args
     * @param arg contains all the arguments needed to make an operation
     * @return a string with either the result of the operation or an error message
     */
    private String showOperation(String arg) {
        String[] args = arg.split(" ");
        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("COMPUTE")) {
                if (isNumeric(args[2]) && isNumeric(args[3])) {
                    double x = Double.parseDouble(args[2]);
                    double y = Double.parseDouble(args[3]);
                    if (args[1].equalsIgnoreCase("ADD")){
                        return String.format("> %.1f", x + y);
                    } else if (args[1].equalsIgnoreCase("SUB")) {
                        return String.format("> %.1f", x - y);
                    } else if (args[1].equalsIgnoreCase("MUL")) {
                        return String.format("> %.1f", x * y);
                    } else if (args[1].equalsIgnoreCase("DIV")) {
                        if (y != 0)
                            return String.format("> %.1f", x / y);
                        return "> Cannot divide by 0.";
                    }
                }
            }
        }
        return "> Error: invalid command.";
    }

    /**
     * Helper showing all the commands and operations
     * @return string showing a list of commands and operations
     */
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
                    out.println(showOperation(line));
                    out.flush();
                }
                LOG.info("From client: " + line);
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