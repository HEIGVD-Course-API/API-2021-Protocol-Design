package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
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
     * Shows current_value to client
     * @param show_prompt should show "> " on new line or not
     */
    private void show_current_value(boolean show_prompt) {
        out.println(current_value);
        if (show_prompt)
            show_prompt();
        else
            out.flush();
    }

    /**
     * Check if value is zero
     * Permit to factorize check if we want to change type used
     * @param value to check
     * @return value == 0
     */
    private static boolean is_zero(Double value) {
        return (value == 0);
    }

    /**
     * Set current_value to value and send it to client
     * @param value new value
     */
    private void set(Double value) {
        current_value = value;
        show_current_value(true);
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
    private void subtract(Double value) {
        set(current_value - value);
    }

    /**
     * Multiply current value by value and send it to client
     * @param value to multiply
     */
    private void multiply(Double value) {
        set(current_value * value);
    }

    /**
     * Divide current value by value and send it to user
     * Could send client error "DIVIDE_BY_ZERO" if value == 0
     * @param value to use as divider
     */
    private void divide(Double value) {
        if (is_zero(value)) {
            show_error(ErrorCode.DIVIDE_BY_ZERO);
            return;
        }
        set(current_value / value);
    }

    /**
     * Apply modulus of value to current value and send it to client
     * Could send client error "DIVIDE_BY_ZERO" if value == 0
     * @param value to use as modulus
     */
    private void modulus(Double value) {
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
            case UNKNOWN_COMMAND:
                msg = "Unkown command syntax";
                break;
            case INVALID_INPUT:
                msg = "Invalid input";
                break;
            case UNKNOWN_OPERATOR:
                msg = "Unknown operator used";
                break;
            case UNKNOWN_FUNC:
                msg = "Unkown function used";
                break;
            default:
                msg = "Unknown error";
        }

        out.println("ERR: " + code + " " + msg);
        show_prompt();
    }

    private void show_prompt() {
        out.print("> ");
        out.flush();
    }

    /**
     * Convert string to value and check for errors
     * @throws NumberFormatException if string can't be converted to Double
     * @param value_to_convert explicit
     * @return converted value
     */
    private Double convert(String value_to_convert) throws NumberFormatException {
        try {
            return Double.parseDouble(value_to_convert);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Can't convert string to double !", e);
            show_error(ErrorCode.INVALID_INPUT);
            throw e;
        }
    }

    /**
     * Execute current_value = current_value op value;
     * @param op operator to apply
     * @param value value to use for calculation
     */
    private void execOp(String op, Double value) {
        // Checks that operators exists
        if (!Operator.is_valid(op)) {
            show_error(ErrorCode.UNKNOWN_OPERATOR);
            return;
        }

        // Convert string operator to enum
        OP e_op = Operator.to_enum(op);

        // NOTE : As operator is checked with is_valid, then it will never be null !
        // So we can ignore this warning, everything is safe
        // Execute operator on value
        switch(e_op) {
            case PLUS:
                add(value);
                break;
            case MINUS:
                subtract(value);
                break;
            case MULT:
                multiply(value);
                break;
            case DIVIDE:
                divide(value);
                break;
            case MODULUS:
                modulus(value);
                break;
            case AFFECT:
                set(value);
                break;
        }
    }

    /**
     * Execute op on current value with op
     * current_value = current_value op value;
     * Example : current_value = current_value + 12;
     * @param op operator to execute
     * @param val value to apply
     */
    private void execOp(String op, String val) {
        double value;

        // Convert value to double
        try {
            value = convert(val);
        } catch (NumberFormatException ignore) {return;}

        execOp(op, value);
    }

    /**
     * Execute function on current val then execute operator with new val
     * current_value = val op func(val);
     * @param op operator to execute
     * @param func math function to execute
     * @param val val to use
     */
    private void execFunc(String op, String func, String val) {
        double value;

        // Convert value to double
        try {
            value = convert(val);
        } catch (NumberFormatException e) {return;}

        // Checks that the function is known
        if (!Function.is_valid(func)) {
            show_error(ErrorCode.UNKNOWN_FUNC);
            return;
        }

        // Convert function to enum
        FUNC e_func = Function.to_enum(func);

        // NOTE : As we tested that the function exists, it will never be null
            // Then, we can ignore the warning
        // Apply function on val
        switch (e_func) {
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

        // Execute operation with the new val
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
        out.println("'value' : Set current_value to value\n");
        out.println("'OP value' : Execute current_value OP value");
        out.println("Supported operators : " + Operator.list() + "\n");
        out.println("'OP FUNC value' : Execute current_value OP FUNC(value)");
        out.println("Supported functions : " + Function.list() + "\n");
        out.print("Current value : ");

        // Set starting value to 0 and send it to client
        set((double) 0);

        // Communication with clients
        String line = null;        // Read line from reader
        String[] commands;         // Commands splitted from line

        LOG.info("WORKER - Reading on #"+id+" until client sends BYE or closes the connection...");
        while(!Thread.interrupted()) {
            // Try to read line received
            try {
                line = in.readLine();
            } catch (IOException e) {
                if (ended)
                    LOG.info("CLIENT - Thread stopped");
                else
                    LOG.log(Level.SEVERE, "WORKER - Can't read on client #"+id+" !", e);
            }

            // Check that line is valid
            if (line == null) {
                end_client("line == null");
                return;
            }

            // If line == "bye", end of communication
            if (line.equalsIgnoreCase("bye")) {
                LOG.info("WORKER - Closed by client on #"+id+" !");
                end_client("bye from client");
                return;
            }

            // Splits line received to multiple
            commands = line.split(" ");

            // Execute received command
            switch (commands.length) {
                case 1: // NUMBER
                    execOp("=", commands[0]);
                    break;
                case 2: // OP NUMBER
                    execOp(commands[0], commands[1]);
                    break;
                case 3: // OP FUNC NUMBER
                    execFunc(commands[0], commands[1], commands[2]);
                    break;
                default: // Unknown case
                    show_error(ErrorCode.UNKNOWN_COMMAND);
                    break;
            }
        }

        end_client("End of program");
    }

    /**
     * Clean end of thread
     * @param caller who try to end the thread
     */
    public void end_client(String caller) {
        // Close only once
        if (ended) return;
        ended = true;

        LOG.info("WORKER - Closing client #"+id+", called by " + caller);

        // Send end message to client
        if (out != null) {
            try {
                out.print("Last value : ");
                show_current_value(false); // Sends the final result
                out.println("BYE");
                out.flush();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "WORKER - Can't say bye to #"+id+" !", e);
            }
        }

        // Closes writer
        if (out != null) {
            try {
                out.close();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "WORKER - Can't close writer on #"+id+" !", e);
            }
        }

        // Closes reader
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "WORKER - Can't close reader on "+id+" !", e);
            }
        }

        // Closes client socket
        LOG.info("WORKER - Cleaning ressources for #" + id);
        if (clientSocket != null) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "WORKER - Can't close client socket !", e);
            }
        }
    }
}