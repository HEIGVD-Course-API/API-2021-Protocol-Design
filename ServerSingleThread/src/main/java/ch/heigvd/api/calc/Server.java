package ch.heigvd.api.calc;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator server implementation - single threaded
 */
public class Server {

  private enum CALC_MESSAGE {
    NOTIFY, HELP, COMPUTE, RESULT, ERROR, QUIT
  }

  private enum CALC_SUPPORTED_OPERATIONS {
    ADD_2, ADD_3, ADD_4, DIV_2, FAC_1, MUL_2, POW_2, SUB_2;

    @Override
    public String toString() {
      return String.join(" ", this.name().split("_"));
    }

    public String getArgsNb() {
      return this.name().split("_")[1];
    }
  }

  private enum CALC_ERROR_CODES {
    NONE(0), BAD_REQUEST(400), NOT_FOUND(404), INTERNAL_SERVER_ERROR(500);

    private final int _code;

    private CALC_ERROR_CODES(final int code) {
      _code = code;
    }

    public int code() {
      return _code;
    }

    @Override
    public String toString() {
      return _code + " " + String.join(" ", this.name().split("_"));
    }
  }

  private final int PORT = 3193;

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
    /*
     * TODO: implement the receptionist server here. The receptionist just creates a
     * server socket and accepts new client connections. For a new client
     * connection, the actual work is done by the handleClient method below.
     */
    ServerSocket serverSocket = null;
    try {
      serverSocket = new ServerSocket(PORT);
      Socket clientSocket;
      while (true) {
        clientSocket = serverSocket.accept();
        handleClient(clientSocket);
      }
    } catch (IOException e) {
      LOG.log(Level.SEVERE, e.toString(), e);
      return;
    } finally {
      try {
        if (serverSocket != null && !serverSocket.isClosed())
          serverSocket.close();
      } catch (IOException e) {
        LOG.log(Level.SEVERE, e.toString(), e);
      }
    }
  }

  /**
   * Handle a single client connection: receive commands and send back the result.
   *
   * @param clientSocket with the connection with the individual client.
   */
  private void handleClient(Socket clientSocket) {

    /*
     * TODO: implement the handling of a client connection according to the
     * specification. The server has to do the following: - initialize the dialog
     * according to the specification (for example send the list of possible
     * commands) - In a loop: - Read a message from the input stream (using
     * BufferedReader.readLine) - Handle the message - Send to result to the client
     */
    BufferedReader in = null;
    BufferedWriter out = null;

    LOG.info("New connection from " + clientSocket.toString());

    try {
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
      out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));

      out.write(CALC_MESSAGE.NOTIFY + "\r\n");
      out.flush();
      for (CALC_SUPPORTED_OPERATIONS o : CALC_SUPPORTED_OPERATIONS.values()) {
        out.write(o + "\r\n");
        out.flush();
      }
      out.write("END " + CALC_MESSAGE.NOTIFY + "\r\n");
      out.flush();

      String cmd;
      while ((cmd = in.readLine()) != null) {
        LOG.info("Recevied command: " + cmd);
        if (cmd.equals(CALC_MESSAGE.QUIT.name())) {
          LOG.info("Closing connection with " + clientSocket.toString());
          break;
        }
        if (cmd.contains(CALC_MESSAGE.COMPUTE.name())) {
          // Checks if the request contains at least 3 args: COMPUTE <OPERATION> <OP1> ...
          String[] args = cmd.split(" ");
          if (args.length < 3) {
            out.write(CALC_ERROR_CODES.NOT_FOUND + "\r\n");
            out.flush();
            continue;
          }

          // Checks if all the arguments after the operation are integers
          int[] operands;
          try {
            operands = Arrays.stream(Arrays.copyOfRange(args, 2, args.length)).mapToInt(Integer::parseInt).toArray();
          } catch (NumberFormatException e) {
            out.write(CALC_ERROR_CODES.BAD_REQUEST + "\r\n");
            out.flush();
            continue;
          }

          // Checks if the requested operation is supported
          String operation = args[1] + "_" + operands.length;
          String operationString = args[1] + " " + operands.length;
          CALC_SUPPORTED_OPERATIONS supportedOperation;
          try {
            supportedOperation = CALC_SUPPORTED_OPERATIONS.valueOf(operation);
          } catch (IllegalArgumentException e) {
            out.write(CALC_ERROR_CODES.NOT_FOUND + ": " + operationString + "\r\n");
            out.flush();
            continue;
          }

          // Computes operation
          int response = 0;
          CALC_ERROR_CODES error = CALC_ERROR_CODES.NONE;
          switch (supportedOperation) {
          case ADD_2:
          case ADD_3:
          case ADD_4:
            response = Arrays.stream(operands).sum();
            break;
          case DIV_2:
            if (operands[1] == 0) {
              error = CALC_ERROR_CODES.INTERNAL_SERVER_ERROR;
            } else {
              response = operands[0] / operands[1];
            }
            break;
          case FAC_1:
            if (operands[0] < 0 || operands[0] > 10)
              error = CALC_ERROR_CODES.INTERNAL_SERVER_ERROR;
            else {
              response = 1;
              for (int i = 2; i < operands[0]; i++) {
                response *= i;
              }
            }
            break;
          case MUL_2:
            response = operands[0] * operands[1];
            break;
          case POW_2:
            response = (int) Math.pow(operands[0], operands[1]);
            break;
          case SUB_2:
            response = operands[0] - operands[1];
            break;
          }
          if (error != CALC_ERROR_CODES.NONE) {
            out.write(error + "\r\n");
            out.flush();
          } else {
            out.write(CALC_MESSAGE.RESULT + " " + response + "\r\n");
            out.flush();
          }
          LOG.info("Sent response");
        } else {
          out.write(CALC_ERROR_CODES.NOT_FOUND + "\r\n");
          out.flush();
        }
      }

    } catch (Exception e) {
      LOG.log(Level.SEVERE, e.toString(), e);
    } finally {
      try {
        if (in != null)
          in.close();
      } catch (IOException e) {
        LOG.log(Level.SEVERE, e.toString(), e);
      }
      try {
        if (out != null)
          out.close();
      } catch (IOException e) {
        LOG.log(Level.SEVERE, e.toString(), e);
      }

      try {
        if (clientSocket != null && !clientSocket.isClosed()) {
          out.close();
        }
      } catch (IOException e) {
        LOG.log(Level.SEVERE, e.toString(), e);
      }
    }
  }
}