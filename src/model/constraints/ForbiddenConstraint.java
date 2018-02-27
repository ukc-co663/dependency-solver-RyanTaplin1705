package model.constraints;

import repository.model.Package;

import java.util.LinkedList;

public class ForbiddenConstraint extends Constraint {

    // must not have any of these packages in final packages...
    public LinkedList<Package> packages;

    public ForbiddenConstraint(LinkedList<Package> packages) {
        this.packages = packages;
    }
}
