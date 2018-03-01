package repository;

import model.states.State;
import model.states.ValidState;
import repository.model.Package;

import java.util.LinkedList;
import java.util.List;

public class OptionalPackages extends LinkedList<Package> {

    private LinkedList<Package> packages;

    public OptionalPackages(LinkedList<Package> packages) {
        this.packages = packages;
    }

    public List<ValidState> getAllValid(State state, PackageRepository repository) throws Exception {
        LinkedList<ValidState> result = new LinkedList<>();
        for(Package p : packages) {
            LinkedList<State> clones = state.clone().addPackage(p, repository);
            for (State s : clones) {
                if (s.isValid()) result.add((ValidState)s);
            }
        }
        return result;
    }

    //use this as a wrapper for dependencies that are optional (i.e pick one of them).
}
