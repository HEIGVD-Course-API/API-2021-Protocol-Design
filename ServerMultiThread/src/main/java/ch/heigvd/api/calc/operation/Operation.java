package ch.heigvd.api.calc.operation;

public abstract class Operation {
    public abstract int doOperation(String[] args);
    public abstract int getNbOperands();
}
