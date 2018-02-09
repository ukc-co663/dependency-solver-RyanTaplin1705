package repository.model;

import java.util.List;

public class Dependency {

    public String name;
    public String version;
    public int size;

    public List<Dependants> dependants;
    public List<Conflict> conflicts;

    public Dependency(String name, String version, int size, List<Dependants> dependants, List<Conflict> conflicts) {
        this.name = name;
        this.version = version;
        this.size = size;
        this.dependants = dependants;
        this.conflicts = conflicts;
    }
}