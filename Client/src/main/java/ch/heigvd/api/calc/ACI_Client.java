package ch.heigvd.api.calc;

import java.util.Objects;
import java.util.logging.Level;

public class ACI_Client extends Client{
    private final String[] operations = {"ADD", "SUB", "MUL"};
    private final String[] statusCode = {"RES", "ERR", "CLO"};
    public enum operation {ADD, SUB, MUL}
    public int operation(operation op, int... operands){
        StringBuilder msg = new StringBuilder(operations[op.ordinal()]);
        for (int operand: operands) {
            msg.append(" ").append(operand);
        }
        send(msg.toString());
        return waitAndTranslateResponse();
    }
    private int waitAndTranslateResponse(){
        String[] response = receive().split(" ");
        String statusCode = response[0];
        switch(statusCode){
            case "RES":
                return Integer.getInteger(response[1]);
            case "CLO":
            case "ERR":
                LOG.log(Level.SEVERE, response[1]);
                throw new RuntimeException("error when receiving data " + response[1]);
        }
        return 0;
    }

    public void close(){
        send("CLO\n");
        String[] response = receive().split(" ");
        String statusCode = response[0];
        if(Objects.equals(statusCode, "CLO"))
            super.close();
    }
}
