package machine;

import model.AddInstruction;
import model.RemoveInstruction;
import repository.DependencyRepository;
import repository.model.Conflict;
import repository.model.Dependants;
import repository.model.Dependency;

import java.util.HashMap;

import static repository.model.Operation.NONE;

public class State {

    public DependencyRepository repository;
    public HashMap<String, Dependency> dependenciesState;

    public State(DependencyRepository repository, HashMap<String, Dependency> dependencies) {
        this.repository = repository;
        this.dependenciesState = dependencies;
    }

    /*
       1. Checks if the dependency exists in the repo.
       2. Check if valid state can be obtained for this dep. (some manual interaction for uninstalling deps?)
       3. Incrementally install; checking state at each step.
     */
    public void install(AddInstruction instruction) throws Exception {
        Dependency dependency = repository.getDependency(instruction.getName());
        if (dependency != null && !installed(dependency)) {
            incrementallyInstall(instruction);
        } else {
            throw new Exception("Invalid state. Either " + instruction.getName() + " does not exist in the repository or it is already installed.");
        }
    }

    private void incrementallyInstall(AddInstruction instruction) {
        //TODO
    }

    public void uninstall(RemoveInstruction instruction) throws Exception {
        Dependency dependency = repository.getDependency(instruction.getName());
        if (dependency != null && installed(dependency)) {
            incrementallyUninstall(instruction);
        } else {
            throw new Exception("Invalid state. Either " + instruction.getName() + " is not installed.");
        }
    }

    private void incrementallyUninstall(RemoveInstruction instruction) {
        //TODO
    }

    private boolean installed(Dependency dependency) {
        return dependenciesState.containsKey(dependency.name);
    }

    /* TODO: need more logic in here; checking the version >=, <=, <, >, = */
    public static boolean isValid(HashMap<String, Dependency> state) {
        for(Dependency depTo : state.values()) {
            for (Dependency depFrom : state.values()) {
                if (depFrom.conflicts.contains(asConflict(depTo)) || depTo.dependants.contains(asDependant(depFrom)))
                    return false;
            }
        }
        return true;
    }

    //TODO: not liking this setup to much, rethink this.
    private static Dependants asDependant(Dependency dependency) {
        return new Dependants(dependency.name, dependency.version, NONE);
    }

    private static Conflict asConflict(Dependency dependency) {
        return new Conflict(dependency.name, dependency.version, NONE);
    }
}
