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
    BufferedReader in = null;
    BufferedWriter out = null;

    LOG.info("New connection from " + clientSocket.toString());

    try {
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
      out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));

      out.write(Calc.MESSAGES.NOTIFY + "\r\n");
      out.flush();
      for (Calc.SUPPORTED_OPERATIONS o : Calc.SUPPORTED_OPERATIONS.values()) {
        out.write(o + "\r\n");
        out.flush();
      }
      out.write("END " + Calc.MESSAGES.NOTIFY + "\r\n");
      out.flush();

      String cmd;
      while ((cmd = in.readLine()) != null) {
        LOG.info("Recevied command: " + cmd);
        if (cmd.equals(Calc.MESSAGES.QUIT.name())) {
          LOG.info("Closing connection with " + clientSocket.toString());
          break;
        }
        if (cmd.contains(Calc.MESSAGES.COMPUTE.name())) {
          // Checks if the request contains at least 3 args: COMPUTE <OPERATION> <OP1> ...
          String[] args = cmd.split(" ");
          if (args.length < 3) {
            out.write(Calc.ERROR_CODES.NOT_FOUND + "\r\n");
            out.flush();
            continue;
          }

          // Checks if all the arguments after the operation are integers
          int[] operands;
          try {
            operands = Arrays.stream(Arrays.copyOfRange(args, 2, args.length)).mapToInt(Integer::parseInt).toArray();
          } catch (NumberFormatException e) {
            out.write(Calc.ERROR_CODES.BAD_REQUEST + "\r\n");
            out.flush();
            continue;
          }

          // Checks if the requested operation is supported
          String operation = args[1] + "_" + operands.length;
          String operationString = args[1] + " " + operands.length;
          Calc.SUPPORTED_OPERATIONS supportedOperation;
          try {
            supportedOperation = Calc.SUPPORTED_OPERATIONS.valueOf(operation);
          } catch (IllegalArgumentException e) {
            out.write(Calc.ERROR_CODES.NOT_FOUND + ": " + operationString + "\r\n");
            out.flush();
            continue;
          }

          // Computes operation
          int response = 0;
          Calc.ERROR_CODES error = Calc.ERROR_CODES.NONE;
          switch (supportedOperation) {
          case ADD_2:
          case ADD_3:
          case ADD_4:
            response = Arrays.stream(operands).sum();
            break;
          case DIV_2:
            if (operands[1] == 0) {
              error = Calc.ERROR_CODES.INTERNAL_SERVER_ERROR;
            } else {
              response = operands[0] / operands[1];
            }
            break;
          case FAC_1:
            if (operands[0] < 0 || operands[0] > 10)
              error = Calc.ERROR_CODES.INTERNAL_SERVER_ERROR;
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
          if (error != Calc.ERROR_CODES.NONE) {
            out.write(error + "\r\n");
            out.flush();
          } else {
            out.write(Calc.MESSAGES.RESULT + " " + response + "\r\n");
            out.flush();
          }
          LOG.info("Sent response");
        }
        // Request for help
        else if (cmd.contains(Calc.MESSAGES.HELP.name())) {
          String[] args = cmd.split(" ");
          if (args.length != 3) {
            out.write(Calc.ERROR_CODES.BAD_REQUEST + "\r\n");
            out.flush();
          } else {
            // Checks if the requested operation is supported
            String operation = args[1] + "_" + args[2];
            String operationString = args[1] + " " + args[2];
            Calc.SUPPORTED_OPERATIONS supportedOperation;
            try {
              supportedOperation = Calc.SUPPORTED_OPERATIONS.valueOf(operation);
            } catch (IllegalArgumentException e) {
              out.write(Calc.ERROR_CODES.NOT_FOUND + ": " + operationString + "\r\n");
              out.flush();
              continue;
            }
            out.write(supportedOperation + ": " + supportedOperation.description() + "\r\n");
            out.flush();
          }
        } else {
          out.write(Calc.ERROR_CODES.NOT_FOUND + "\r\n");
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