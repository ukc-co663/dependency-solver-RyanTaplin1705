package repository.model;

public class Conflicts {

    public String name;
    public String version;
    public Operation operation;

    public Conflicts(String name, String version, Operation operation) {
        this.name = name;
        this.version = version;
        this.operation = operation;
    }
}
