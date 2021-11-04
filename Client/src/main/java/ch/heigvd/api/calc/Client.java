package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static java.lang.Character.isDigit;
import static java.lang.Character.isWhitespace;

/**
 * Calculator client implementation
 */
public class Client {

    private static final Logger LOG = Logger.getLogger(Client.class.getName());

    private static String parse(String input) {
        int operatorPosition = 0;
        boolean operatorReaded = false;
        boolean error = false;

        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);

            if (!isDigit(c) && c != '-' && c != '+' && c != '*' && c != '/' && c != '%' && c != '^' && c != '.') {
                error = true;
                break;
            }

            if (c == '-') { //gestion of -
                if (i == 0) { //first position means the sign of the first operant
                    continue;
                } else if (i == operatorPosition + 1 && operatorReaded) { //sign of second operand
                    continue;
                } else if (!operatorReaded) { //operator if there is already an operator
                    operatorPosition = i;
                    operatorReaded = true;
                } else { //error
                    error = true;
                    break;
                }
            }

            if (c == '+' || c == '*' || c == '/' || c == '%' || c == '^') { //every other operators
                if (!operatorReaded) { //checks if there is already an operator
                    operatorPosition = i;
                    operatorReaded = true;
                } else {
                    error = true;
                    break;
                }
            }
        }

        if (!error) {
            String op1Str = input.substring(0, operatorPosition);
            char operator = input.charAt(operatorPosition);
            String op2Str = input.substring(operatorPosition + 1);

            double op1 = Double.parseDouble(op1Str);
            double op2 = Double.parseDouble(op2Str);

            return operator + ";" + op1 + ";" + op2 + "\n";


        } else {
            return "Error";
        }
    }

    /**
     * Main function to run client
     *
     * @param args no args required
     */
    public static void main(String[] args) throws IOException {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        //create a socket and bind it to port 2341, localhost
        Socket clientSocket = null;
        BufferedWriter clientOut;
        BufferedReader clientIn;
        try{
            clientSocket = new Socket("127.0.0.1", 2341);
            clientOut = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        }catch (Exception e){
            LOG.log(Level.SEVERE, "Error while creating socket", e);
            return;
        }


        String input = "";
        do {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            //create parse user input and send it to server

            try {
                input = br.readLine().toLowerCase();
            }catch (Exception e){
                LOG.log(Level.SEVERE, "Error while reading user input", e);
                return;
            }
            //delete all whitespace in the input
            input = input.replaceAll("\\s+","");
            if(input.compareTo("stop")!= 0){
                String out = parse(input);
                if(out.compareTo("Error") == 0){
                    System.out.println("input error");
                    continue;
                }

                clientOut.write(out+'\n');
                clientOut.flush();

                String resultat = clientIn.readLine();
                System.out.println(resultat);
            } else {
                System.out.println("bye");
            }


        }while(input.compareTo("stop") != 0);


        /* TODO: Implement the client here, according to your specification
         *   The client has to do the following:
         *   - connect to the server
         *   - initialize the dialog with the server according to your specification
         *   - In a loop:
         *     - read the command from the user on stdin (already created)
         *     - send the command to the server
         *     - read the response line from the server (using BufferedReader.readLine)
         */




    }
}
