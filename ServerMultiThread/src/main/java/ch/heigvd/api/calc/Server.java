package ch.heigvd.api.calc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator server implementation - multi-thread
 */
public class Server {

  private final static Logger LOG = Logger.getLogger(Server.class.getName());

  private final int PORT = 3193;

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
     * connection, the actual work is done in a new thread by a new ServerWorker.
     */
    ServerSocket serverSocket = null;
    try {
      serverSocket = new ServerSocket(PORT);
      Socket clientSocket;
      while (true) {
        LOG.info("Waiting for client...");
        clientSocket = serverSocket.accept();
        LOG.info("New connection from " + clientSocket.toString());
        ServerWorker sw = new ServerWorker(clientSocket);
        Thread thread = new Thread(sw);
        thread.start();
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
}
