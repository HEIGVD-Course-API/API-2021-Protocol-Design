package ch.heigvd.api.calc;

abstract class Operation {
    private final String name;
    private final int nbOperands;

    protected Operation(String name, int nbOperands) {
        this.name = name;
        this.nbOperands = nbOperands;
    }

    public String getName() {
        return name;
    }

    public int getNbOperands() {
        return nbOperands;
    }

    @Override
    public String toString() {
        return  name + ' ' + nbOperands;
    }

    public abstract ServerCommand applyOp(double[] operands);
}
