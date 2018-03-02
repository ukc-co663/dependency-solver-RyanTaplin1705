package model.states.types;

import model.constraints.ForbiddenConstraint;
import model.states.FinalState;
import repository.PackageRepository;

import java.util.List;

public interface Valid extends State {
    FinalState inspection(List<ForbiddenConstraint> inspectors, PackageRepository packageRepository) throws Exception;
}
