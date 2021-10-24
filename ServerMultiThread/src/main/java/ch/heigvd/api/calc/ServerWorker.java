package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO : Overflow check
/**
 * Calculator worker implementation
 */
public class ServerWorker implements Runnable {
    private final static Logger LOG = Logger.getLogger(ServerWorker.class.getName());
    private final Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private Double current_value;

    /** CONSTRUCTOR
     * Instantiation of a new worker mapped to a socket
     *
     * @param clientSocket connected to worker
     */
    public ServerWorker(Socket clientSocket) throws IOException {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        this.clientSocket = clientSocket;

        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Can't open reader !", e);
            throw e;
        }

        try {
            out = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Can't open writer !", e);
            throw e;
        }
    }
    private void show_current_value() {
        out.println(current_value);
        out.flush();
    }
    private boolean is_zero(Double value) {
        return (value == 0);
    }
    private void set(Double value) {
        current_value = value;
        show_current_value();
    }
    private void add(Double value) {
        set(current_value + value);
    }
    private void sub(Double value) {
        set(current_value - value);
    }
    private void mult(Double value) {
        set(current_value * value);
    }
    private void div(Double value) {
        if (is_zero(value)) {
            show_error(ErrorCode.DIVIDE_BY_ZERO);
            return;
        }
        set(current_value / value);
    }
    private void mod(Double value) {
        if (is_zero(value)) {
            show_error(ErrorCode.DIVIDE_BY_ZERO);
            return;
        }
        set(current_value % value);
    }

    private void show_error(ErrorCode code) {
        String msg;

        switch(code) {
            case DIVIDE_BY_ZERO:
                msg = "Can't divide by zero !";
                break;
            case OVERFLOW:
                msg = "Overflow";
                break;
            default:
                msg = "Unknown error";
        }

        out.println("ERR: " + code + " " + msg);
        out.flush();
    }
    private Double convert(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            show_error(ErrorCode.INVALID_INPUT);
            LOG.log(Level.SEVERE, "Invalid input : " + value, e);
            throw e;
        }
    }
    private void execOp(String op, Double value) {
        if (!Operator.is_valid(op)) {
            show_error(ErrorCode.UNKNOWN_OPERATOR);
            return;
        }

        OP e_op = Operator.to_enum(op);

        switch(Objects.requireNonNull(e_op)) {
            case PLUS:
                add(value);
                break;
            case MINUS:
                sub(value);
                break;
            case MULT:
                mult(value);
                break;
            case DIVIDE:
                div(value);
                break;
            case MODULUS:
                mod(value);
                break;
        }
    }
    private void execFunc(String op, String func, Double value) {
        if (!Function.is_valid(func)) {
            show_error(ErrorCode.UNKNOWN_FUNC);
            return;
        }

        FUNC e_func = Function.to_enum(func);

        // Appliquer la fonction et modifie value
        switch (Objects.requireNonNull(e_func)) {
            case COS:
                value = Math.cos(value);
                break;
            case SIN:
                value = Math.sin(value);
                break;
            case TAN:
                value = Math.tan(value);
                break;
            case ARCSIN:
                value = Math.asin(value);
                break;
            case ARCCOS:
                value = Math.acos(value);
                break;
            case ARCTAN:
                value = Math.atan(value);
                break;
            case SQRT:
                value = Math.sqrt(value);
                break;
        }

        execOp(op, value);
    }

    /**
     * Run method of the thread.
     */
    @Override
    public void run() {
        // INIT
        out.println("Hello ! Supported commands :");
        out.println("'value' : Set current value to 'value'");
        out.println("'OP value' : Execute current_value OP value");
        out.println("OP supported : " + Operator.list());
        out.println("'OP FUNC value' : Execute current_value OP FUNC(value)");
        out.println("FUNC supported : " + Function.list());

        // Défini la valeur de départ à 0
        set((double) 0);

        // Échanges avec le client
        String line;        // Read ligne from reader
        String[] commands;  // Commands splitted from line
        Double value;   // Converted value
        try {
            LOG.info("Reading until client sends BYE or closes the connection...");
            while ((line = in.readLine()) != null) {
                // Fin du programme, sortie
                if (line.equalsIgnoreCase("bye")) break;

                commands = line.split(" ");

                // Execute la commande reçue
                switch(commands.length) {
                    case 1: // NUMBER
                        try {
                            value = convert(commands[0]);
                        } catch (Exception ignored) {
                            break;
                        }

                        set(value);
                        break;
                    case 2: // OP NUMBER
                        try {
                            value = convert(commands[1]);
                        } catch (Exception ignored) {
                            break;
                        }

                        execOp(commands[0], value);
                        break;
                    case 3: // OP FUNC NUMBER
                        try {
                            value = convert(commands[2]);
                        } catch (Exception ignored) {
                            break;
                        }

                        execFunc(commands[0], commands[1], value);
                        break;
                    default:
                        show_error(ErrorCode.UNKNOWN_COMMAND);
                        break;
                }
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error while receiving messages ! End of transmission", e);
        } finally {
            terminaison();
        }
    }

    private void terminaison() {
        LOG.info("Closing");

        if (out != null) {
            try {
                out.println("BYE");
                out.flush();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Can't say bye bye !", e);
            }
        }

        LOG.info("Cleaning ressources");

        if (clientSocket != null) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Can't close client socket !", e);
            }
        }

        if (out != null) {
            try {
                out.close();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Can't close writer !", e);
            }
        }

        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Can't close reader !", e);
            }
        }
    }
}