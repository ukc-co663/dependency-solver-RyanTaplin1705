package repository;

import repository.model.Package;

import java.util.HashMap;
import java.util.LinkedList;

public class PackageRepository implements Repository {

    private HashMap<String, LinkedList<Package>> dependencies;

    public PackageRepository(HashMap<String, LinkedList<Package>> packages) {
        this.dependencies = packages;
    }

    public HashMap<String, LinkedList<Package>> getAllDependencies() {
        return dependencies;
    }

    public RepositoryPackages getDependency(String name) {
        return new RepositoryPackages(dependencies.get(name));
    }
}
