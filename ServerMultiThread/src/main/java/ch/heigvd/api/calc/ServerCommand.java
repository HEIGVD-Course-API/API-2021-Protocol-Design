package ch.heigvd.api.calc;

abstract class ServerCommand {
    private String name;

    protected ServerCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract String toString();
}
