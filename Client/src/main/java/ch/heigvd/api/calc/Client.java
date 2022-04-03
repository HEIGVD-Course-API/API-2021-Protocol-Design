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

        stdin = new BufferedReader(new InputStreamReader(System.in));

        Socket socket = new Socket("127.0.0.1", 1997);
        BufferedReader receiveReader = new BufferedReader( new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII) );
        PrintWriter sendWriter = new PrintWriter( socket.getOutputStream(), true, StandardCharsets.US_ASCII);

        String line;
        while( !(line = receiveReader.readLine()).equals("END") )
        {
            System.out.println(line);
        }

        System.out.println(line);

        boolean quit = false;
        while( !quit && (line = stdin.readLine()) != null)
        {
            sendWriter.println(line);

            quit = line.equals("GOODBYYE");

            if (!quit)
            {
                System.out.println( receiveReader.readLine() );
            }
        }

        socket.close();
    }
}
