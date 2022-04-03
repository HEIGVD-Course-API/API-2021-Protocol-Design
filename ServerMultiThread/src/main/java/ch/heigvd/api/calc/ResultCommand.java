package ch.heigvd.api.calc;

public class ResultCommand extends ServerCommand {
    private double result;

    public ResultCommand(double result) {
        super("RESULT");
        this.result = result;
    }

    @Override
    public String toString() {
        return getName() + " " + result;
    }
}
