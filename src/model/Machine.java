package model;

import model.constraints.Constraint;
import model.constraints.ForbiddenConstraint;
import model.constraints.InstallConstraint;
import model.states.FinalState;
import model.states.State;
import model.states.ValidState;
import repository.PackageRepository;
import repository.model.Package;

import java.util.LinkedList;
import java.util.List;

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

    public FinalState satisfyConstraints(List<Constraint> constraints) throws Exception {
        List<InstallConstraint> installed = new LinkedList<>();
        List<ForbiddenConstraint> inspectors = new LinkedList<>();

        for (Constraint c : constraints) {
            if (c instanceof InstallConstraint) installed.add((InstallConstraint)c);
            else if (c instanceof ForbiddenConstraint) inspectors.add((ForbiddenConstraint)c);
            else throw new Exception("Constraint type did not match..");
        }
        LinkedList<ValidState> validStates = getValid(processInstallations(installed));
        LinkedList<FinalState> solutions = inspectSolutions(inspectors, validStates);
        return optimal(solutions); // if we have multiple solutions return the highest
    }

    private LinkedList<State> processInstallations(List<InstallConstraint> installed) throws Exception {
        LinkedList<State> solutions = new LinkedList<>();
        for (InstallConstraint c : installed) {
            for (Package p : c.optional.packages) { //for every package in OptionalPackages
                State s = state.clone();
                solutions.addAll(s.addPackage(p, packageRepository));
            }
        }
        return solutions;
    }

    private LinkedList<ValidState> getValid(LinkedList<State> states) {
        LinkedList<ValidState> result = new LinkedList<>();
        for (State s : states) {
            if (s.isValid()) {
                result.add((ValidState)s);
            }
        }
        return result;
    }

    private LinkedList<FinalState> inspectSolutions(List<ForbiddenConstraint> inspectors, LinkedList<ValidState> solutions) throws Exception {
        //sweep in at the end with inspectors. try to uninstall forbidden constraints or reject
        LinkedList<FinalState> finals = new LinkedList<>();
        for(ValidState s : solutions) {
            FinalState fss = s.inspection(inspectors, packageRepository);
            if (fss != null) finals.add(fss);
        }
        return finals;
    }

    private FinalState optimal(LinkedList<FinalState> states) throws Exception {
        if (states.size() == 1) return states.getFirst(); //return state or get fastest if multiple exist
        FinalState winningState = states.getFirst();
        for (FinalState s : states) {
            if (s.cost() < winningState.cost()) winningState = s;
        }
        return winningState;
    }
}
