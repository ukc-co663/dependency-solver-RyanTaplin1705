package model;

import model.instructions.AddInstruction;
import model.instructions.Instruction;
import model.instructions.RemoveInstruction;
import model.exceptions.InvalidStateException;
import org.json.JSONArray;
import repository.DependencyRepository;
import repository.model.Conflict;
import repository.model.Dependants;
import repository.model.Dependency;

import java.util.*;
import java.util.stream.Collectors;

import static model.exceptions.InvalidStateException.REASON_CONFLICT;
import static util.Printer.stringFormat;
import static util.Setup.*;

public class State {

    private final HashMap<String, LinkedList<Conflict>> constraints;
    public DependencyRepository repository;
    public List<Instruction> history = new ArrayList<>();
    public HashMap<String, Dependency> dependenciesState;

    public State(String repositoryPath, String initialStatePath, String constraintsPath) throws Exception {
        this.repository = new DependencyRepository(getRepository(repositoryPath));
        this.constraints = getConstraints(constraintsPath);
        this.dependenciesState = getInitialState(initialStatePath, repository);
    }

    public void printHistory() {
        System.out.println(new JSONArray(stringFormat(history)));
    }

    public void processInstructions(List<Instruction> instructions) throws Exception {
        for(int i = 0; i < instructions.size(); i++) {
            processInstruction(instructions.get(i));
        }
    }

    public void install(AddInstruction instruction) throws Exception {
        Dependency dependency = getDependencyOfVersion(instruction.getVersion(), repository.getDependency(instruction.getName()));
        if (dependency != null) {
            if (!installed(dependency.name, dependency.version)) {
                history.add(instruction);
                incInstallDeps(dependency.deps);
            } // don't reinstall anything already installed.
        } else {
            throw new InvalidStateException("Invalid state. Either " + instruction.getName() + " does not exist in the repository.");
        }
    }

    public void uninstall(RemoveInstruction instruction) throws Exception {
        Dependency dependency = getDependencyOfVersion(instruction.getVersion(), repository.getDependency(instruction.getName()));
        if (dependency != null && installed(dependency.name, dependency.version) && notConstraint(dependency.name, dependency.version)) {
            history.add(instruction);
            incUninstallRedundantDeps(dependency.deps);
        } else  throw new InvalidStateException("Invalid state. " + instruction.getName() + " is not installed.");
    }

    private void processInstruction(Instruction instruction) throws Exception {
        if (dependenciesState.size() < 1) {
            instruction.run(this);
        } else {
            Collection<Dependency> curInstalls = dependenciesState.values();
            for(Dependency dependency : curInstalls) {
                List<Conflict> conflicts = dependency.conflictsWith(instruction.getName(), instruction.getVersion());
                if (conflicts.size() > 0) throw new InvalidStateException(REASON_CONFLICT);
            }
            instruction.run(this);
        }
    }

    private void incInstallDeps(List<Dependants> deps) throws Exception {
        for (int i = 0; i < deps.size(); i++) {
            Dependants dep = deps.get(i);
            if (!installed(dep.getName(), dep.getVersion()) && dependenciesState.containsKey(dep.getName() + "=" + dep.getVersion())) {
                List<Dependency> collect = dependenciesState.values().stream()
                        .filter(d -> d.conflictsWith(dep.getName(), dep.getVersion()).size() > 0).collect(Collectors.toList());
                if (collect.size() > 0) throw new InvalidStateException(REASON_CONFLICT);
                else install(new AddInstruction(dep.getName(), dep.getVersion()));
            }
        }
    }

    private void incUninstallRedundantDeps(List<Dependants> deps) throws Exception {
        for (int i = 0; i < deps.size(); i++) {
            Dependants dep = deps.get(i);
            if (installed(dep.getName(), dep.getVersion())) {
                List<Dependency> collect = dependenciesState.values().stream()
                        .filter(d -> d.requiredWith(dep.getName(), dep.getVersion()).size() > 0).collect(Collectors.toList());
                if (collect.size() > 0) throw new InvalidStateException(REASON_CONFLICT);
                else uninstall(new RemoveInstruction(dep.getName(), dep.getVersion()));
            }
        }
    }

    private boolean notConstraint(String name, String version) {
        return constraints.get(name).stream().filter(c -> c.getVersion() == version).collect(Collectors.toList()).size() <= 0;
    }

    private boolean installed(String name, String version) {
        return dependenciesState.containsKey(name + "=" + version);
    }
}
