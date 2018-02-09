package machine;

import model.AddInstruction;
import model.Instruction;
import model.RemoveInstruction;
import org.json.JSONArray;
import repository.DependencyRepository;
import repository.model.Conflict;
import repository.model.Dependency;
import sun.plugin.dom.exception.InvalidStateException;
import util.FileWriter;

import java.io.IOException;
import java.util.*;

import static repository.model.Operation.*;
import static util.Setup.getDependencyOfVersion;
import static util.VersionChecker.versionGreaterThan;

public class State {

    public DependencyRepository repository;

    public List<Instruction> history;

    public HashMap<String, Dependency> dependenciesState;

    public State(DependencyRepository repository, HashMap<String, Dependency> dependencies) {
        this.repository = repository;
        this.dependenciesState = dependencies;
    }

    public void processInstructions(List<Instruction> instructions) throws Exception {
        for(int i = 0; i < instructions.size(); i++) {
            processInstruction(instructions.get(i));
        }
    }

    /*
       1. Checks if the dependency exists in the repo.
       2. Check if valid state can be obtained for this dep. (some manual interaction for uninstalling deps?)
       3. Incrementally install; checking state at each step.
     */
    public void install(AddInstruction instruction) throws Exception {
        Dependency dependency = getDependencyOfVersion(instruction.getVersion(), repository.getDependency(instruction.getName()));
        if (dependency != null && !installed(dependency)) {
            incrementallyInstall(instruction);
            history.add(instruction);
        } else {
            throw new Exception("Invalid state. Either " + instruction.getName() + " does not exist in the repository or it is already installed.");
        }
    }

    public void uninstall(RemoveInstruction instruction) throws Exception {
        Dependency dependency = getDependencyOfVersion(instruction.getVersion(), repository.getDependency(instruction.getName()));
        if (dependency != null && installed(dependency)) {
            incrementallyUninstall(instruction);
            history.add(instruction);
        } else {
            throw new Exception("Invalid state. Either " + instruction.getName() + " is not installed.");
        }
    }

    public void writeHistory() throws IOException {
        FileWriter.create("commands.json").writeJSON(new JSONArray(history));
    }

    private void incrementallyInstall(AddInstruction instruction) {
        //TODO
    }

    private void incrementallyUninstall(RemoveInstruction instruction) {
        //TODO
    }

    private boolean installed(Dependency dependency) {
        return dependenciesState.containsKey(dependency.name) && dependenciesState.get(dependency.name).version.equals(dependency.version);
    }

    private void processInstruction(Instruction instruction) throws Exception {
        if (dependenciesState.size() < 1) {
            instruction.run(this);
        } else {
            Collection<Dependency> curInstalls = dependenciesState.values();
            for(Dependency dependency : curInstalls) {
                List<Conflict> conflicts = dependency.conflictsWith(instruction);
                if (conflicts.size() > 0) {
                    resolveConflicts(conflicts, instruction);
                }
            }
            instruction.run(this);
        }
    }

    private void resolveConflicts(List<Conflict> conflicts, Instruction instruction) throws Exception {
        for(int i = 0; i < conflicts.size(); i++) {
            Conflict conflict = conflicts.get(i);
            removeConflict(conflict);
            installResolvedConflict(conflict.name, findNewVersion(instruction, conflict));
        }
    }

    private String findNewVersion(Instruction instruction, Conflict conflict) throws Exception {
        if (conflict.operation == GREATER_THAN || conflict.operation == GREATER_THAN_OR_EQUAL_TO) {
            if (versionGreaterThan(conflict.version, instruction.getVersion()))
                return getDowngradedVersion(conflict);
        } else if (conflict.operation == LESS_THAN || conflict.operation == LESS_THAN_OR_EQUAL_TO) {
            if (!versionGreaterThan(conflict.version, instruction.getVersion()))
                return getUpgradedVersion(conflict);
        }
        throw new InvalidStateException("Can't find a lower/higher version.");
    }

    private void installResolvedConflict(String name, String version) throws Exception {
        AddInstruction addInstruction = new AddInstruction(name, version);
        install(addInstruction);
    }

    private void removeConflict(Conflict conflict) throws Exception {
        RemoveInstruction removeInstruction = new RemoveInstruction(conflict.name, conflict.version);
        uninstall(removeInstruction);
    }

    private List<Conflict> getAllConflicts(Dependency dep) {
        List<Conflict> conflicts = new ArrayList<>();
        Collection<Dependency> curInstalls = dependenciesState.values();
        for(Dependency dependency : curInstalls) {
            conflicts.addAll(dependency.conflictsWith(dep));
        }
        return conflicts;
    }

    private String getUpgradedVersion(Conflict conflict) throws Exception {
        LinkedList<Dependency> dependencies = repository.getDependency(conflict.name);
        for (int i = 0; i > dependencies.size(); i++) {
            Dependency dependency = dependencies.get(i);
            if (versionGreaterThan(conflict.version, dependency.version) && getAllConflicts(dependency).size() <= 0)
                return dependency.version;
        }
        throw new Exception("This package can't be upgraded.");
    }

    private String getDowngradedVersion(Conflict conflict) throws Exception {
        LinkedList<Dependency> dependencies = repository.getDependency(conflict.name);
        for (int i = dependencies.size(); i > 0; i--) {
            Dependency dependency = dependencies.get(i);
            if (!versionGreaterThan(conflict.version, dependency.version) && getAllConflicts(dependency).size() <= 0)
                return dependency.version;
        }
        throw new Exception("This package can't be downgraded.");
    }
}
