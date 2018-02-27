package repository;

import repository.model.Package;

import java.util.LinkedList;

public class OptionalPackages extends LinkedList<Package> {

    private LinkedList<Package> packages;

    public OptionalPackages(LinkedList<Package> packages) {
        this.packages = packages;
    }

    //use this as a wrapper for dependencies that are optional (i.e pick one of them).
}
