package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Calculator worker implementation */
public class ServerWorker implements Runnable {

  private static final Logger LOG = Logger.getLogger(ServerWorker.class.getName());

  private Socket clientSocket = null;
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

    try {
      this.clientSocket = clientSocket;
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      out = new PrintWriter(clientSocket.getOutputStream());
    } catch (IOException ex) {
      Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /** Run method of the thread. */
  @Override
  public void run() {
    try {
      out.write(
          "Welcome to your online calculator ! "
              + "Compute with '{add|sub|mul} x y'. Close connexion with 'bye'\n");
      out.flush();
      String line;
      while (!clientSocket.isClosed()) {
        while ((line = in.readLine()) != null) {
          // reads until client close connexion
          String[] s = line.split(" ");
          if (s.length < 3) {
            if (s[0].equalsIgnoreCase("bye")) {
              out.write("Goodbye !\n");
              out.flush();
              clientSocket.close();
              System.out.println("Client disconnected");
              break;
            }
            sendErrorCode();
            continue;
          }
          float a;
          float b;
          float answer;

          try {
            a = Float.parseFloat(s[1]);
            b = Float.parseFloat(s[2]);
          } catch (NumberFormatException e) {
            sendErrorCode();
            continue;
          }

          switch (s[0]) {
            case "add":
              answer = a + b;
              break;
            case "sub":
              answer = a - b;
              break;
            case "mul":
              answer = a * b;
              break;
            default:
              sendErrorCode();
              continue;
          }
          sendAnswer(answer);
        }
      }
    } catch (IOException e) {
      Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, e);
    }
  }

  private void sendErrorCode() {
    out.write(Code.ERROR.toString() + "\n");
    out.flush();
  }

  private void sendAnswer(float answer) {
    out.write(Code.OK + " " + answer + "\n");
    out.flush();
  }

  private enum Code {
    OK(0),
    ERROR(1);
    private final int value;

    Code(int val) {
      value = val;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
  }
}
