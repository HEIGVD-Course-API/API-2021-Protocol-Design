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
    private final BufferedReader in;
    private final PrintWriter out;
    private Double current_value;
    private static int ids = 0;
    private static final int id = ids++;
    private boolean ended = false;

    /** CONSTRUCTOR
     * Instantiation of a new worker mapped to a socket
     *
     * @param clientSocket connected to worker
     */
    public ServerWorker(Socket clientSocket) throws IOException {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        // Saving client socket
        this.clientSocket = clientSocket;

        // Trying to open Reader
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "WORKER - Can't open reader on #"+id+" !", e);
            throw e;
        }

        // Trying to open writer
        try {
            out = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "WORKER - Can't open writer on #"+id+" !", e);
            throw e;
        }
    }

    /**
     * Send current value to client
     */
    private void show_current_value() {
        out.println(current_value);
        out.flush();
    }

    /**
     * Check if value is zero
     * Permit to standardize check if we want to change type used
     * @param value to check
     * @return value == 0
     */
    private boolean is_zero(Double value) {
        return (value == 0);
    }

    /**
     * Set current value to value and send it to client
     * @param value new value
     */
    private void set(Double value) {
        current_value = value;
        show_current_value();
    }

    /**
     * Add value to current value and send it to client
     * @param value to add
     */
    private void add(Double value) {
        set(current_value + value);
    }

    /**
     * Subtract value to current value and send it to client
     * @param value to subtract
     */
    private void sub(Double value) {
        set(current_value - value);
    }

    /**
     * Multiply current value by value and send it to client
     * @param value to multiply
     */
    private void mult(Double value) {
        set(current_value * value);
    }

    /**
     * Divide current value by value and send it to user
     * @param value to use as divider
     */
    private void div(Double value) {
        if (is_zero(value)) {
            show_error(ErrorCode.DIVIDE_BY_ZERO);
            return;
        }
        set(current_value / value);
    }

    /**
     * Apply modulus of value to current value and send it to client
     * @param value to use as modulus
     */
    private void mod(Double value) {
        if (is_zero(value)) {
            show_error(ErrorCode.DIVIDE_BY_ZERO);
            return;
        }
        set(current_value % value);
    }

    /**
     * Send error to client
     * @param code error to send
     */
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

    /**
     * Convert string to value and check for errors
     * @param value_to_convert explicit
     * @return converted value
     */
    private Double convert(String value_to_convert) {
        try {
            return Double.parseDouble(value_to_convert);
        } catch (Exception e) {
            show_error(ErrorCode.INVALID_INPUT);
            throw e;
        }
    }

    /**
     * Execute op on current value with op
     * current_value = current_value op value;
     * @param op operator to execute
     * @param value value to apply
     */
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

    /**
     * Execute function on current value then execute operator with new value
     * current_value = value op func(value);
     * @param op operator to execute
     * @param func math function to execute
     * @param value value to use
     */
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
        out.println("Hello ! Welcome on our awesome calculator !");
        out.println("Supported commands :");
        out.println("value : Set current_value to value");
        out.println("OP value : Execute current_value OP value");
        out.println("Supported operators : " + Operator.list());
        out.println("OP FUNC value : Execute current_value OP FUNC(value)");
        out.println("Supported functions : " + Function.list());

        // Set starting value to 0 and send it to client
        set((double) 0);


        // Communication with clients
        String line = null;        // Read ligne from reader
        String[] commands;         // Commands splitted from line
        Double value;              // Converted value

        LOG.info("WORKER - Reading on #"+id+" until client sends BYE or closes the connection...");
        while(!Thread.interrupted()) {
            try {
                line = in.readLine();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "WORKER - Can't read on client #"+id+" !", e);
            }

            if (line == null) {
                terminaison("line == null");
                return;
            }

            if (line.equalsIgnoreCase("bye")) {
                LOG.info("WORKER - Closed by client on #"+id+" !");
                terminaison("bye");
                return;
            }

            commands = line.split(" ");

            // Execute la commande reçue
            switch (commands.length) {
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

        terminaison("End of program");
    }

    /**
     * Clean end of thread
     * @param caller who try to end the thread
     */
    public void terminaison(String caller) {
        // Close only once
        if (ended) return;
        ended = true;

        LOG.info("WORKER - Closing client #"+id+" by " + caller);

        // Send end message
        if (out != null) {
            try {
                out.println("BYE");
                out.flush();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "WORKER - Can't say bye to #"+id+" !", e);
            }
        }

        LOG.info("WORKER - Cleaning ressources for #" + id);
        if (clientSocket != null) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "WORKER - Can't close client socket !", e);
            }
        }

        if (out != null) {
            try {
                out.close();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "WORKER - Can't close writer on #"+id+" !", e);
            }
        }

        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "WORKER - Can't close reader on "+id+" !", e);
            }
        }
    }
}