package ch.heigvd.res.examples;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements a single-threaded TCP server. It is able to interact
 * with only one client at the time. If a client tries to connect when
 * the server is busy with another one, it will have to wait.
 * 
 * @author Olivier Liechti
 */
public class SingleThreadedServer {
		
	final static Logger LOG = Logger.getLogger(SingleThreadedServer.class.getName());
	
	int port;
	
	/**
	 * Constructor
	 * @param port the port to listen on 
	 */
	public SingleThreadedServer(int port) {
		this.port = port;
	}
	
	/**
	 * This method initiates the process. The server creates a socket and binds
	 * it to the previously specified port. It then waits for clients in a infinite
	 * loop. When a client arrives, the server will read its input line by line
	 * and send back the data converted to uppercase. This will continue until
	 * the client sends the "BYE" command.
	 */
	public void serveClients() {
		ServerSocket serverSocket;
		Socket clientSocket = null;
		BufferedReader in = null;
		BufferedWriter out = null;
		
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, null, ex);
			return;
		}
				
		while (true) {
			try {
				
				LOG.log(Level.INFO, "Single-threaded: Waiting for a new client on port {0}", port);
				clientSocket = serverSocket.accept();
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
				out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
				String line;

				out.write("Welcome to the Single-Threaded Server.\nSend me text lines and conclude with the BYE command.\n");
				out.flush();
				LOG.info("Reading until client sends BYE or closes the connection...");				
				while ( (line = in.readLine()) != null ) {
					if (line.equalsIgnoreCase("bye")) {
						break;
					}
					out.write("> " + line.toUpperCase() + "\n");
					out.flush();
				}
				
				LOG.info("Cleaning up resources...");				
				clientSocket.close();
				in.close();
				out.close();
				
			} catch (IOException ex) {
				if (in != null) {
					try {
						in.close();
					} catch (IOException ex1) {
						LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException ex1) {
						LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
					}
				}
				if (clientSocket != null) {
					try {
						clientSocket.close();
					} catch (IOException ex1) {
						LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
					}
				}
				LOG.log(Level.SEVERE, ex.getMessage(), ex);
			}
		}	
	}
}
