package ch.heigvd.api.calc.operation;

public abstract class Operation {
    public abstract int doOperation(String[] args) throws RuntimeException;
    public abstract int getNbOperands();
}
