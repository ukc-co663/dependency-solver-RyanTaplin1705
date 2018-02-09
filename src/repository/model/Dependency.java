package repository.model;

import java.util.List;
import java.util.stream.Collectors;

public class Dependency {

    public String name;
    public String version;
    public int size;

    public List<Conflict> confs;
    public List<Dependants> deps;

    public Dependency(String name, String version, int size, List<Conflict> conf, List<Dependants> deps) {
        this.name = name;
        this.version = version;
        this.size = size;
        this.confs = conf;
        this.deps = deps;
    }

    public List<Conflict> conflictsWith(String name, String version) {
        return this.confs.stream().map(conflict -> {
            if (conflict.getName().equals(name) &&
                    conflict.isConflicting(version)) return conflict;
            else return null;
        }).collect(Collectors.toList());
    }

    public List<Dependants> requiredWith(String name, String version) {
        return this.deps.stream().map(dep -> {
            if (dep.getName().equals(name) && !dep.isRequired(version)) return dep;
            else return null;
        }).collect(Collectors.toList());
    }
}
