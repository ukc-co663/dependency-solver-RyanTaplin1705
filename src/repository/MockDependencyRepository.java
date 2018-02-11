package repository;

import repository.model.Dependency;

import java.util.HashMap;
import java.util.LinkedList;

public class MockDependencyRepository implements Repository {

    private HashMap<String, LinkedList<Dependency>> dependencies;

    public MockDependencyRepository(HashMap<String, LinkedList<Dependency>> dependencies) {
        this.dependencies = dependencies;
    }

    public HashMap<String, LinkedList<Dependency>> getAllDependencies() {
        return dependencies;
    }

    public LinkedList<Dependency> getDependency(String name) {
        return this.dependencies.get(name);
    }
}
