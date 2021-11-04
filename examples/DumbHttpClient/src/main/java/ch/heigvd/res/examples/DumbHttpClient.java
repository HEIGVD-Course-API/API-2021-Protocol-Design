package ch.heigvd.res.examples;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is not really an HTTP client, but rather a very simple program that
 * establishes a TCP connection with a real HTTP server. Once connected, the 
 * client sends "garbage" to the server (the client does not send a proper
 * HTTP request that the server would understand). The client then reads the
 * response sent back by the server and logs it onto the console.
 * 
 * @author Olivier Liechti
 */
public class DumbHttpClient {

	static final Logger LOG = Logger.getLogger(DumbHttpClient.class.getName());

	/**
	 * This method does the whole processing
	 */
	public void sendWrongHttpRequest() {
		Socket clientSocket = null;
		BufferedWriter out = null;
		BufferedReader in = null;
		
		try {
			clientSocket = new Socket("www.heig-vd.ch", 80);
			out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			String malformedHttpRequest = "Hello, sorry, but I don't speak HTTP...\r\n\r\n";
			out.write(malformedHttpRequest);
			out.flush();

			LOG.log(Level.INFO, "*** Response sent by the server: ***");
			String line;
			while ((line = in.readLine()) != null) {
				LOG.log(Level.INFO, line);
			}
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, ex.toString(), ex);
		} finally {
			try {
				if (out != null) out.close();
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, ex.toString(), ex);
			}
			try {
				if (in != null) in.close();
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, ex.toString(), ex);
			}
			try {
				if (clientSocket != null && ! clientSocket.isClosed()) clientSocket.close();
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, ex.toString(), ex);
			}
		}
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

		DumbHttpClient client = new DumbHttpClient();
		client.sendWrongHttpRequest();
	}
}