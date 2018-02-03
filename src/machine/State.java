package machine;

import repository.model.Conflicts;
import repository.model.Dependants;
import repository.model.Dependency;
import repository.DependencyRepository;

import java.util.List;

import static repository.model.Operation.NONE;

public class State {

    public DependencyRepository repository;
    public List<Dependency> dependenciesState;

    public State(DependencyRepository repository, List<Dependency> dependencies) {
        this.repository = repository;
        this.dependenciesState = dependencies;
    }

    /*
       1. Checks if the dependency exists in the repo.
       2. Check if valid state can be obtained for this dep. (some manual interaction for uninstalling deps?)
       3. Incrementally install; checking state at each step.
     */
    public void install(String dependencyName) {
        Dependency dependency = repository.getDependency(dependencyName);
        if (dependency != null && !installed(dependency) && stateValid(dependency))
            dependenciesState = incrementallyInstall(dependency);
    }

    private boolean stateValid(Dependency dependency) {
        return repository.getAllDependencies().containsKey(dependency.name);
    }

    private boolean installed(Dependency dependency) {
        return dependenciesState.contains(dependency);
    }

    private List<Dependency> incrementallyInstall(Dependency dependency) {
        List<Dependency> initialState = dependenciesState;

        return null;
    }

    public void uninstall(String dependencyName) {

    }

    /* TODO: need more logic in here; checking the version >=, <=, <, >, = */
    public static boolean isValid(List<Dependency> state) {
        for(Dependency depTo : state) {
            for (Dependency depFrom : state) {
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

    private static Conflicts asConflict(Dependency dependency) {
        return new Conflicts(dependency.name, dependency.version, NONE);
    }
}
