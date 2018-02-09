package repository.model;

import model.Instruction;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<Conflict> conflictsWith(Dependency dependency) {
        return this.conflicts.stream().map(conflict -> {
            if (conflict.name.equals(dependency.name) &&
                    conflict.isConflicting(dependency.version)) return conflict;
            else return null;
        }).collect(Collectors.toList());
    }

    public List<Conflict> conflictsWith(Instruction instruction) {
        return this.conflicts.stream().map(conflict -> {
            if (conflict.name.equals(instruction.getName()) &&
                    conflict.isConflicting(instruction.getVersion())) return conflict;
            else return null;
        }).collect(Collectors.toList());
    }
}
