package model.constraints;

import java.util.LinkedList;
import java.util.List;

public class ConstraintsPair {

    public LinkedList<InstallConstraint> install = new LinkedList<>();
    public LinkedList<ForbiddenConstraint> forbidden = new LinkedList<>();

    public ConstraintsPair(List<Constraint> constraints) throws Exception {
        for (Constraint c : constraints) {
            if (c instanceof InstallConstraint) install.add((InstallConstraint)c);
            else if (c instanceof ForbiddenConstraint) forbidden.add((ForbiddenConstraint)c);
            else throw new Exception("Constraint type did not match..");
        }
    }
}
