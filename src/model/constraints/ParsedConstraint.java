package model.constraints;

import model.Operation;

public class ParsedConstraint {

    public String name;
    public String version;
    public Operation op;

    public ParsedConstraint(String name, String version, Operation op) {
        this.name = name;
        this.version = version;
        this.op = op;
    }
}
