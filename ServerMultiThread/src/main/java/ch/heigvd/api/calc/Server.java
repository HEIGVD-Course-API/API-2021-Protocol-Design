package ch.heigvd.api.calc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Calculator server implementation - multi-thread */
public class Server {

  private final int PORT = 3003;

  private static final Logger LOG = Logger.getLogger(Server.class.getName());

  /** Main function to start the server */
  public static void main(String[] args) {
    // Log output on a single line
    System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

    (new Server()).start();
  }

  /** Start the server on a listening socket. */
  private void start() {
    try {
      // Create the server socket
      ServerSocket serverSocket = new ServerSocket(PORT);

      // Waiting for new clients to connect
      while (true) {
        Socket clientSocket = serverSocket.accept();

        // Create new thread to handle this client
        ServerWorker worker = new ServerWorker(clientSocket); // create worker
        Thread thread = new Thread(worker); // create thread with worker
        thread.start(); // start thread
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
}
