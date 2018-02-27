package repository;

import repository.model.Package;

import java.util.LinkedList;

/**
 * This class is a representation of returning multiple packages from the repository.
 */
public class ConflictPackages extends LinkedList<Package> {

    public LinkedList<Package> packages;

    public ConflictPackages(LinkedList<Package> packages) {
        this.packages = packages;
    }

}
