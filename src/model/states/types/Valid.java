package model.states.types;

import model.constraints.ForbiddenConstraint;

import java.util.List;

public interface Valid {
    boolean isValid();
    boolean isFinal();
    List<Final> inspection(List<ForbiddenConstraint> inspectors) throws Exception;
}
