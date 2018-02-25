package model;

import model.exceptions.InvalidInstructionException;
import model.exceptions.InvalidStateException;
import model.instructions.AddInstruction;
import model.instructions.Instruction;
import model.instructions.RemoveInstruction;
import repository.DependencyRepository;
import repository.model.Conflict;
import repository.model.Dependant;
import repository.model.Dependency;

import java.util.*;
import java.util.stream.Collectors;

import static model.exceptions.InvalidStateException.REASON_CONFLICT;
import static util.Setup.*;

public class State {

    private final HashMap<String, LinkedList<Conflict>> constraints;
    private final DependencyRepository repository;

    public List<Instruction> history = new ArrayList<>();
    public HashMap<String, Dependency> state;

    public State(String repositoryPath, String initialStatePath, String constraintsPath) throws Exception {
        this.repository = new DependencyRepository(readRepository(repositoryPath));
        this.constraints = readConstraints(constraintsPath);
        this.state = readInitialState(initialStatePath, repository);
    }

    public DependencyRepository getRepository() {
        return repository;
    }

    public void install(AddInstruction instruction) throws Exception {
        //TODO: what happens in case where version is empty? (i.e all Deps) ~ need to retrace and deal with this edge case
        Dependency dependency = getDependencyOfVersion(instruction.getVersion(), repository.getDependency(instruction.getName()));
        if (dependency != null) {
            if (!installed(dependency.name, dependency.version)) {
                history.add(instruction);
                incInstallDependants(dependency.dependants);
            }
        } else throw new InvalidStateException("Invalid state. Either " + instruction.getName() + " does not exist in the repository.");
    }

    public void uninstall(RemoveInstruction instruction) throws Exception {
        Dependency dependency = getDependencyOfVersion(instruction.getVersion(), repository.getDependency(instruction.getName()));
        if (dependency != null && installed(dependency.name, dependency.version)) {
            if (isAConstraint(dependency.name, dependency.version)) throw new InvalidInstructionException("Can't uninstall constraint.");
            else {
                history.add(instruction);
                incUninstallDependants(dependency.dependants);
            }
        } else  throw new InvalidStateException("Invalid state. " + instruction.getName() + " is not installed.");
    }

    public List<Dependency> conflicts(String name, String version) {
        return state.values().stream()
                        .filter(d -> d.conflictsWith(name, version).size() > 0).collect(Collectors.toList());
    }

    //checks if a dep in state conflicts, if it does try to uninstall that dep.
    private void incInstallDependants(List<Dependant> dependants) throws Exception {
        for(Dependant d : dependants) {
            List<Dependency> conflicts = conflicts(d.getName(), d.getVersion());
            if (!conflicts.isEmpty() && canUninstall(conflicts)) uninstallConflicts(conflicts);
            install(new AddInstruction(d.getName(), d.getVersion()));
        }
    }

    private void uninstallConflicts(List<Dependency> conflicts) throws Exception {
        for (Dependency d : conflicts) {
            RemoveInstruction removeInstruction = new RemoveInstruction(d.name, d.version);
            uninstall(removeInstruction);
        }
    }

    // check against all constraint dependants to see if these conflicts are required.
    // if they are required then {false} otherwise {true}
    public boolean canUninstall(List<Dependency> conflicts) throws Exception {
        // check if conflict(s) is equal to constraints or constraint deps
        for(Dependency d : conflicts) {
            for (List<Conflict> consList : constraints.values()) {
                for (Conflict cons: consList) {
                    Dependency constraint = getDependencyOfVersion(cons.getVersion(), repository.getDependency(cons.getName()));
                    if (!constraint.conflictsWith(d.name, d.version).isEmpty()) return false;
                }
            }
        }
        return true;
    }

    // dep -> [op1,op2]
    // if constraints -> conflicts -> equals dep -> true
    private void incUninstallDependants(List<Dependant> deps) throws Exception {
        for (Dependant dep : deps) {
            if (installed(dep.getName(), dep.getVersion())) {
                List<Dependency> collect = state.values().stream()
                        .filter(d -> d.requiredBy(dep.getName(), dep.getVersion()).size() > 0).collect(Collectors.toList());
                if (collect.size() > 0) throw new InvalidStateException(REASON_CONFLICT);
                else uninstall(new RemoveInstruction(dep.getName(), dep.getVersion()));
            } else throw new InvalidStateException(dep.getName() + "=" + dep.getVersion() + " is not installed");
        }
    }

    private boolean isAConstraint(String name, String version) {
        return constraints.get(name).stream().filter(c -> c.getVersion().equals(version)).collect(Collectors.toList()).size() <= 0;
    }

    private boolean installed(String name, String version) {
        return state.containsKey(name + "=" + version);
    }
}
