package ch.heigvd.api.calc;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Calculator server implementation - single threaded
 */
public class Server {
    private final String WELCOME_MSG = "Welcome \n  FOLLOWING OPERATIONS ARE AVAILABLE\n" +
            "            ADD\n" +
            "            SUB\n" +
            "            MULT\n" +
            "            DIV\n" +
            "\n";

    //No enum class sadly....
    public static final int ADD = 1;
    public static final int SUB = 2;
    public static final int MULT = 3;
    public static final int DIV = 4;
    public static final int UNKNOWN = -1;
    public static final String UNKNOWN_MSG = "Unknown Operation \n";
    public static final int WRONG_ARGUMENTS = -2;
    public static final String WRONG_ARG_MSG = "Two numbers are required to process your calculus !\n";


    private final String UNKNOWN_OPERATION = "ERROR 1 UNKNOWN OPERATION";

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

        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(1339);
            while (true) {
                System.out.println("SingleThreaded: Waiting for client to connect\n");
                clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return;
        }

    }

    private void sendWelcomeMessage(BufferedWriter out) throws IOException {

        out.write(WELCOME_MSG);
        out.flush();

    }

    private Calculus parseClientMessage(BufferedReader in) throws IOException {

        ArrayList<Integer> numbers = new ArrayList<>();
        Calculus calculus = new Calculus(numbers, -1);

        String clientRequest = in.readLine();

        Scanner scanner = new Scanner(clientRequest).useDelimiter("[^0-9]+");
        while (scanner.hasNextInt())
            calculus.numbers.add(scanner.nextInt());

        String operation = clientRequest.replaceAll("[^a-zA-Z]+", "");

        if (calculus.numbers.size() == 2) {
            switch (operation) {
                case "ADD":
                    calculus.operation = ADD;
                    break;
                case "SUB":
                    calculus.operation = SUB;
                    break;
                case "MULT":
                    calculus.operation = MULT;
                    break;
                case "DIV":
                    calculus.operation = DIV;
                    break;
                default:
                    calculus.operation = UNKNOWN;
            }

        } else {
            calculus.operation = WRONG_ARGUMENTS;
        }
        return calculus;
    }

x    private int processCalculus(Calculus calculus) {

        switch (calculus.operation) {
            case ADD:
                return calculus.numbers.get(0) + calculus.numbers.get(1);
            case SUB:
                return calculus.numbers.get(0) - calculus.numbers.get(1);
            case MULT:
                return calculus.numbers.get(0) * calculus.numbers.get(1);
            case DIV:
                return calculus.numbers.get(0) / calculus.numbers.get(1);
            default:
                //this case is never reached
                return 0;
        }

    }

    private void handleError(int returnCode, BufferedWriter out) throws IOException {

        switch (returnCode) {
            case UNKNOWN:
                out.write(UNKNOWN_MSG);
                break;
            case WRONG_ARGUMENTS:
                out.write(WRONG_ARG_MSG);
                break;
            default:
                out.write("Something strange happened here...\n ");

        }
        out.flush();
        //TODO : Throw qqch qui ferme le clientsocket le in et le out !

    }

    /**
     * Handle a single client connection: receive commands and send back the result.
     *
     * @param clientSocket with the connection with the individual client.
     */
    private void handleClient(Socket clientSocket) throws IOException {

        System.out.println("SingleThreaded: Handling a client\n");

        BufferedReader in = null;
        BufferedWriter out = null;
        Calculus currentCalculus;


        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
        sendWelcomeMessage(out);
        currentCalculus = parseClientMessage(in);

        //If something went wrong during parsing
        if (currentCalculus.operation < 0) {
            handleError(currentCalculus.operation, out);
        }

        out.write("Answer : " + processCalculus(currentCalculus) + "\n");
        out.flush();

        clientSocket.close();
        in.close();
        out.close();

    }
}