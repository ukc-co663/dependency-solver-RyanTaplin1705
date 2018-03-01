package repository.model;

import repository.ConflictPackages;
import repository.OptionalPackages;

import java.util.List;

public class Package {

    public String name;
    public String version;
    public int size;

    public List<ConflictPackages> conflicts;
    public List<OptionalPackages> dependants;

    public Package(String name, String version, int size, List<ConflictPackages> conf, List<OptionalPackages> dependants) {
        this.name = name;
        this.version = version;
        this.size = size;
        this.conflicts = conf;
        this.dependants = dependants;
    }
}
