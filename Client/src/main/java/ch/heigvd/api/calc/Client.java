package ch.heigvd.api.calc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator client implementation
 */
public class Client {
  private enum CALC_MESSAGE {
    NOTIFY, HELP, COMPUTE, RESULT, ERROR, QUIT
  }

  private static final Logger LOG = Logger.getLogger(Client.class.getName());
  private static final String HOST = "localhost";
  private static final int PORT = 3193;

  /**
   * Main function to run client
   *
   * @param args no args required
   */
  public static void main(String[] args) {
    // Log output on a single line
    System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

    BufferedReader stdin = null;
    BufferedReader in = null;
    BufferedWriter out = null;
    Socket clientSocket = null;

    /*
     * TODO: Implement the client here, according to your specification The client
     * has to do the following: - connect to the server - initialize the dialog with
     * the server according to your specification - In a loop: - read the command
     * from the user on stdin (already created) - send the command to the server -
     * read the response line from the server (using BufferedReader.readLine)
     */

    stdin = new BufferedReader(new InputStreamReader(System.in));

    try {
      clientSocket = new Socket(HOST, PORT);
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
      out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));

      // Display supported operations
      String line;
      System.out.println("Supported operations:");
      while ((line = in.readLine()) != null) {
        if (line.equals("END " + CALC_MESSAGE.NOTIFY))
          break;

        if (!line.equals(CALC_MESSAGE.NOTIFY.name()))
          System.out.println(line);
      }

      System.out.println("Enter command:");
      String cmd;
      while ((cmd = stdin.readLine().toUpperCase()) != null) {
        if (cmd.equals(CALC_MESSAGE.QUIT.name())) {
          out.write(cmd + "\r\n");
          out.flush();
          System.out.println("Quitting...");
          break;
        }
        out.write(cmd + "\r\n");
        out.flush();

        String response = in.readLine();
        if (response != null) {
          System.out.println(response);
        }
      }

    } catch (Exception e) {
      LOG.log(Level.SEVERE, e.toString(), e);
    } finally {
      try {
        if (stdin != null)
          stdin.close();
      } catch (IOException e) {
        LOG.log(Level.SEVERE, e.toString(), e);
      }

      try {
        if (in != null && !clientSocket.isClosed())
          in.close();
      } catch (IOException e) {
        LOG.log(Level.SEVERE, e.toString(), e);
      }

      try {
        if (out != null && !clientSocket.isClosed())
          out.close();
      } catch (IOException e) {
        LOG.log(Level.SEVERE, e.toString(), e);
      }

      try {
        if (clientSocket != null && !clientSocket.isClosed())
          clientSocket.close();
      } catch (IOException e) {
        LOG.log(Level.SEVERE, e.toString(), e);
      }
    }
  }
}
