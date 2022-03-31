package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Calculator client implementation
 */
public class Client {

    private static final Logger LOG = Logger.getLogger(Client.class.getName());

    /**
     * Main function to run client
     *
     * @param args no args required
     */
    public static void main(String[] args) throws Exception {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        BufferedReader stdin = null;

        /* TODO: Implement the client here, according to your specification
         *   The client has to do the following:
         *   - connect to the server
         *   - initialize the dialog with the server according to your specification
         *   - In a loop:
         *     - read the command from the user on stdin (already created)
         *     - send the command to the server
         *     - read the response line from the server (using BufferedReader.readLine)
         */

        stdin = new BufferedReader(new InputStreamReader(System.in));

        Socket socket = new Socket("127.0.0.1", 1997);
        BufferedReader receiveReader = new BufferedReader( new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII) );
        PrintWriter sendWriter = new PrintWriter( socket.getOutputStream(), true, StandardCharsets.US_ASCII);

        String line;
        while( !(line = receiveReader.readLine()).equals("END") )
        {
            /*if (!s.equals("WELCOME"))
            {
                String[] parts = line.split("\\s");
                System.out.println( "Server supports operation " + parts[0] + " that takes " + parts[1] + " parameters." );
            }*/
            System.out.println(line);
        }

        System.out.println(line);

        boolean quit = false;
        while( !quit && (line = stdin.readLine()) != null)
        {
            sendWriter.println(line);

            if (!quit)
            {
                System.out.println( receiveReader.readLine() );
            }
        }

        socket.close();

        String[] parts = line.split("\\s");

        try {
            if (parts[0].equals("ADD") && parts.length == 3)
            {
                double a = Double.parseDouble( parts[1] );
                double b = Double.parseDouble( parts[2] );
                System.out.println("RESULT " + (a + b));
            }
        }
        catch (Exception ex)
        {
            System.out.println("ERROR " + ex.getMessage().replace("\r", "").replace("\n", ""));
        }

    }
}
