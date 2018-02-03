package repository;

import repository.model.Dependency;

import java.util.HashMap;

public class DependencyRepository {

    private HashMap<String, Dependency> dependencies;

    public DependencyRepository(HashMap<String, Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public HashMap<String, Dependency> getAllDependencies() {
        return dependencies;
    }

    public Dependency getDependency(String name) {
        return dependencies.get(name);
    }
}
