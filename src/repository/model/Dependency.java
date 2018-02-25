package repository.model;

import java.util.List;
import java.util.stream.Collectors;

public class Dependency {

    public String name;
    public String version;
    public int size;

    public List<Conflict> confs;
    public List<Dependant> dependants;

    public Dependency(String name, String version, int size, List<Conflict> conf, List<Dependant> dependants) {
        this.name = name;
        this.version = version;
        this.size = size;
        this.confs = conf;
        this.dependants = dependants;
    }

    public List<Conflict> conflictsWith(String name, String version) {
        return this.confs.stream().map(conflict -> {
            if (conflict.getName().equals(name) &&
                    conflict.isConflicting(version)) return conflict;
            else return null;
        }).collect(Collectors.toList());
    }

    // not sure i actually understand what this is doing.
    public List<Dependant> requiredBy(String name, String version) {
        return this.dependants.stream().map(dep -> {
            if (dep.getName().equals(name) && !dep.isRequired(version)) return dep;
            else return null;
        }).collect(Collectors.toList());
    }

    public String getKey() {
        return this.name + "=" + this.version;
    }
}
