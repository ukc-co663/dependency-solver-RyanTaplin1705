package model;

import model.constraints.Constraint;
import model.constraints.ForbiddenConstraint;
import model.constraints.InstallConstraint;
import model.states.State;
import model.states.ValidState;
import model.states.VirtualState;
import repository.PackageRepository;
import repository.model.Package;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Machine {

    private final PackageRepository packageRepository;
    private State state;

    public Machine(State state, PackageRepository packageRepository) {
        this.packageRepository = packageRepository;
        this.state = state;
    }

    public static Machine create(State state, PackageRepository repository) {
        return new Machine(state, repository);
    }

    public ValidState satisfyConstraints(List<Constraint> constraints) throws Exception {
        List<InstallConstraint> installed = new LinkedList<>();
        List<ForbiddenConstraint> inspectors = new LinkedList<>();

        for (Constraint c : constraints) {
            if (c instanceof InstallConstraint) installed.add((InstallConstraint)c);
            else if (c instanceof ForbiddenConstraint) inspectors.add((ForbiddenConstraint)c);
            else throw new Exception("Constraint type did not match..");
        }
        LinkedList<ValidState> solutions = removeInvalidSolutions(inspectors, processInstallations(installed));
        return optimal(solutions); // if we have multiple solutions return the highest
    }

    private ValidState optimal(LinkedList<ValidState> states) throws Exception {
        if (states.size() == 1) return states.getFirst(); //return state or get fastest if multiple exist
        ValidState winningState = states.getFirst();
        for (ValidState s : states) {
            if (s.cost() < winningState.cost()) winningState = s;
        }
        return winningState;
    }

    private LinkedList<ValidState> removeInvalidSolutions(List<ForbiddenConstraint> inspectors, LinkedList<ValidState> solutions) {
        //sweep in the end with inspectors; reject any states that contain forbidden constraints.
        for (int i = 0; i < solutions.size(); i++) {
            ValidState vs = solutions.get(i);
            for (ForbiddenConstraint fc : inspectors) {
                for (Package fp : fc.packages) {
                    if (vs.packages.stream().filter(p -> p.name.equals(fp.name)).collect(Collectors.toList()).size() > 0) {
                        solutions.remove(i); //remove all the packages with a forbidden package match.
                    }
                }
            }
        }
        return solutions;
    }

    private LinkedList<ValidState> processInstallations(List<InstallConstraint> installed) throws Exception {
        LinkedList<ValidState> solutions = new LinkedList<>();
        for (InstallConstraint c : installed) {
            for (Package p : c.packages) {
                VirtualState virtualState = state.virtualize();
                List<Package> conflictingPackages = virtualState.getConflicts(p); //conflicts will return a list of packages that conflict with this
                if (conflictingPackages.isEmpty()) solutions.add(virtualState.addPackage(p).complete()); //if no conflicts then go ahead and install
                else {
                    // If there are conflicts try to uninstall until in a packages where we can install.
                    // If you can never get to that packages then it is invalid and reject.
                    for (Package cp : conflictingPackages) {
                        if (!cp.isRequired(state)) virtualState.removePackage(cp);
                    }
                    solutions.add(virtualState.addPackage(p).complete());
                }
            }
        }
        return solutions;
    }
}
