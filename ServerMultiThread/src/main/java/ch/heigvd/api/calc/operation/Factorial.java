package ch.heigvd.api.calc.operation;

public class Factorial extends Operation {
    public int doOperation(String[] args) throws RuntimeException {
        int a = Integer.parseInt(args[1]);
        if (a < 0) {
            throw new RuntimeException("Operand must be positive.");
        }

        return getFactorial(a);
    }

    private int getFactorial(int n) {
        if (n <= 1) {
            return 1;
        }

        return n * getFactorial(n - 1);
    }

    public int getNbOperands() {
        return 1;
    }
}
