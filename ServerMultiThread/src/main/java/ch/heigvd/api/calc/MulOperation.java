package ch.heigvd.api.calc;

public class MulOperation extends Operation {
    public MulOperation() {
        super("MUL", 2);
    }

    @Override
    public ServerCommand applyOp(double[] operands) {
        if (operands.length != getNbOperands())
            return new ErrorCommand("Wrong number of operands");
        return new ResultCommand(operands[0] * operands[1]);
    }
}
