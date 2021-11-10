package ch.heigvd.api.calc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator worker implementation
 */
public class ServerWorker implements Runnable {

  private final static Logger LOG = Logger.getLogger(ServerWorker.class.getName());

  private BufferedReader _in;
  private BufferedWriter _out;
  private Socket _clientSocket;

  /**
   * Instantiation of a new worker mapped to a socket
   *
   * @param clientSocket connected to worker
   */
  public ServerWorker(Socket clientSocket) {
    // Log output on a single line
    System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

    try {
      _clientSocket = clientSocket;
      _in = new BufferedReader(new InputStreamReader(_clientSocket.getInputStream(), StandardCharsets.UTF_8));
      _out = new BufferedWriter(new OutputStreamWriter(_clientSocket.getOutputStream(), StandardCharsets.UTF_8));
    } catch (IOException e) {
      LOG.log(Level.SEVERE, e.toString(), e);
    }
  }

  /**
   * Run method of the thread.
   */
  @Override
  public void run() {
    try {
      _out.write(Calc.MESSAGES.NOTIFY + "\r\n");
      _out.flush();
      for (Calc.SUPPORTED_OPERATIONS o : Calc.SUPPORTED_OPERATIONS.values()) {
        _out.write(o + "\r\n");
        _out.flush();
      }
      _out.write("END " + Calc.MESSAGES.NOTIFY + "\r\n");
      _out.flush();

      String cmd;
      while ((cmd = _in.readLine()) != null) {
        LOG.info("Received command: " + cmd);
        if (cmd.equals(Calc.MESSAGES.QUIT.name())) {
          LOG.info("Closing connection with " + _clientSocket.toString());
          break;
        }

        if (cmd.contains(Calc.MESSAGES.COMPUTE.name())) {
          // Checks if the request contains at least 3 args: COMPUTE <OPERATION> <OP1> ...
          String[] args = cmd.split(" ");
          if (args.length < 3) {
            _out.write(Calc.ERROR_CODES.NOT_FOUND + "\r\n");
            _out.flush();
            continue;
          }

          // Checks if all the arguments after the operation are integers
          int[] operands;
          try {
            operands = Arrays.stream(Arrays.copyOfRange(args, 2, args.length)).mapToInt(Integer::parseInt).toArray();
          } catch (NumberFormatException e) {
            _out.write(Calc.ERROR_CODES.BAD_REQUEST + "\r\n");
            _out.flush();
            continue;
          }

          // Checks if the requested operation is supported
          String operation = args[1] + "_" + operands.length;
          String operationString = args[1] + " " + operands.length;
          Calc.SUPPORTED_OPERATIONS supportedOperation;
          try {
            supportedOperation = Calc.SUPPORTED_OPERATIONS.valueOf(operation);
          } catch (IllegalArgumentException e) {
            _out.write(Calc.ERROR_CODES.NOT_FOUND + ": " + operationString + "\r\n");
            _out.flush();
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
            _out.write(error + "\r\n");
            _out.flush();
          } else {
            _out.write(Calc.MESSAGES.RESULT + " " + response + "\r\n");
            _out.flush();
          }
          LOG.info("Sent response");
        }
        // Request for help
        else if (cmd.contains(Calc.MESSAGES.HELP.name())) {
          String[] args = cmd.split(" ");
          if (args.length != 3) {
            _out.write(Calc.ERROR_CODES.BAD_REQUEST + "\r\n");
            _out.flush();
          } else {
            // Checks if the requested operation is supported
            String operation = args[1] + "_" + args[2];
            String operationString = args[1] + " " + args[2];
            Calc.SUPPORTED_OPERATIONS supportedOperation;
            try {
              supportedOperation = Calc.SUPPORTED_OPERATIONS.valueOf(operation);
            } catch (IllegalArgumentException e) {
              _out.write(Calc.ERROR_CODES.NOT_FOUND + ": " + operationString + "\r\n");
              _out.flush();
              continue;
            }
            _out.write(supportedOperation + ": " + supportedOperation.description() + "\r\n");
            _out.flush();
          }
        } else {
          _out.write(Calc.ERROR_CODES.NOT_FOUND + "\r\n");
          _out.flush();
        }
      }

    } catch (Exception e) {
      LOG.log(Level.SEVERE, e.toString(), e);
    } finally {
      try {
        if (_in != null)
          _in.close();
      } catch (IOException e) {
        LOG.log(Level.SEVERE, e.toString(), e);
      }
      try {
        if (_out != null)
          _out.close();
      } catch (IOException e) {
        LOG.log(Level.SEVERE, e.toString(), e);
      }

      try {
        if (_clientSocket != null && !_clientSocket.isClosed()) {
          _out.close();
        }
      } catch (IOException e) {
        LOG.log(Level.SEVERE, e.toString(), e);
      }
    }
  }
}