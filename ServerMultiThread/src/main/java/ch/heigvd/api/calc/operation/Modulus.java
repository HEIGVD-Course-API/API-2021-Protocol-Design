package ch.heigvd.api.calc.operation;

public class Modulus extends Operation {
    public int doOperation(String[] args) throws RuntimeException {
        int a = Integer.parseInt(args[1]);
        int b = Integer.parseInt(args[2]);
        return a % b;
    }

    public int getNbOperands() {
        return 2;
    }
}
