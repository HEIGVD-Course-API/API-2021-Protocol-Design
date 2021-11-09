package ch.heigvd.api.calc;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Calculator worker implementation
 */
public class ServerWorker implements Runnable {

    private final static Logger LOG = Logger.getLogger(ServerWorker.class.getName());
    private final Socket clientSocket;
    private final Scanner inputScanner;
    private final PrintWriter writer;
    private boolean closeConnexion = false;

    /**
     * Instantiation of a new worker mapped to a socket
     *
     * @param clientSocket connected to worker
     */
    public ServerWorker(Socket clientSocket) throws IOException {
        // Log output on a single line
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%6$s%n");

        this.clientSocket = clientSocket;
        inputScanner = new Scanner(clientSocket.getInputStream(), StandardCharsets.UTF_8);
        writer = new PrintWriter(clientSocket.getOutputStream(), true, StandardCharsets.UTF_8);

    }

    /**
     * Run method of the thread.
     */
    @Override
    public void run() {

        // inputScanner, writer et clientSocket seront automatiquement close() après le try/catch,
        // même en cas d'exception
        try (
                inputScanner;
                writer;
                clientSocket
        ) {
            writer.println("Bienvenue dans la calculatrice");
            while (inputScanner.hasNextLine() && !closeConnexion) {
                handleNextClientMessage();
            }
        } catch (IOException e) {
            System.err.println("Error while serving client");
            e.printStackTrace();
        }
    }

    private void handleNextClientMessage() {
        Scanner messageScanner = new Scanner(inputScanner.nextLine());
        messageScanner.useDelimiter(" ");
        try {
            switch (messageScanner.next()) {
                case "ADD":
                    sendResponse(messageScanner.nextDouble() + messageScanner.nextDouble());
                    break;
                case "SUB":
                    sendResponse(messageScanner.nextDouble() - messageScanner.nextDouble());
                    break;
                case "MUL":
                    sendResponse(messageScanner.nextDouble() * messageScanner.nextDouble());
                    break;
                case "DIV":
                    sendResponse(messageScanner.nextDouble() / messageScanner.nextDouble());
                    break;
                case "QUIT":
                    sendQuitAck();
                    closeConnexion = true;
                    break;
                default:
                    throw new NoSuchElementException();
            }
        } catch (NoSuchElementException e) {
            sendError();
        }
    }

    private void sendResponse(double responseValue) {
        writer.println("RESPONSE " + responseValue);
    }

    private void sendQuitAck() {
        writer.println("ACK CLOSE");
    }

    private void sendError() {
        writer.println("ERROR");
    }
}