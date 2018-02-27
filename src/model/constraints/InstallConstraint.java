package model.constraints;

import repository.model.Package;

import java.util.LinkedList;

public class InstallConstraint extends Constraint {

    // must install one of these packages...
    public LinkedList<Package> packages;

    public InstallConstraint(LinkedList<Package> packages) {
        this.packages = packages;
    }
}
