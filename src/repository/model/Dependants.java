package repository.model;

public class Dependants {

    public String name;
    public String version;
    public Operation operation;

    public Dependants(String name, String version, Operation operation) {
        this.name = name;
        this.version = version;
        this.operation = operation;
    }
}
