package ch.heigvd.api.calc;

public class ErrorCommand extends ServerCommand {
    private String message;

    public ErrorCommand(String message) {
        super("ERROR");
        this.message = message;
    }

    @Override
    public String toString() {
        return getName() + " " + message;
    }
}
