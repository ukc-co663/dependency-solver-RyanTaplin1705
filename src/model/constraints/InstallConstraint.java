package model.constraints;

import repository.OptionalPackages;

public class InstallConstraint extends Constraint {

    // must install one of these packages...
    public OptionalPackages optional;

    public InstallConstraint(OptionalPackages packages) {
        this.optional = packages;
    }
}
