package machine;

import model.AddInstruction;
import model.Instruction;
import model.RemoveInstruction;
import org.json.JSONArray;
import repository.DependencyRepository;
import repository.model.Conflict;
import repository.model.Dependants;
import repository.model.Dependency;
import util.FileWriter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static util.Setup.*;

public class State {

    private final String workingDir;
    private final HashMap<String, LinkedList<Conflict>> constraints;
    public DependencyRepository repository;
    public List<Instruction> history = new ArrayList<>();
    public HashMap<String, Dependency> dependenciesState;

    public State(String basePath) throws Exception {
        this.workingDir = basePath;
        this.repository = new DependencyRepository(getRepository(basePath + "repository.json"));
        this.constraints = getConstraints(basePath + "constraints.json");
        this.dependenciesState = getInitialState(basePath + "initial.json", repository);
    }

    public void processInstructions(List<Instruction> instructions) throws Exception {
        for(int i = 0; i < instructions.size(); i++) {
            processInstruction(instructions.get(i));
        }
    }

    public void install(AddInstruction instruction) throws Exception {
        Dependency dependency = getDependencyOfVersion(instruction.getVersion(), repository.getDependency(instruction.getName()));
        if (dependency != null && !installed(dependency.name, dependency.version) && canBeInstalled(dependency.name, dependency.version)) {
            history.add(instruction);
            incInstallDeps(dependency.deps);
        } else {
            throw new Exception("Invalid state. Either " + instruction.getName() + " does not exist in the repository or it is already installed.");
        }
    }

    private boolean canBeInstalled(String name, String version) {
        return !dependenciesState.containsKey(name + "=" + version);
    }

    private void incInstallDeps(List<Dependants> deps) throws Exception {
        for (int i = 0; i < deps.size(); i++) {
            Dependants dep = deps.get(i);
            if (!installed(dep.getName(), dep.getVersion()) && dependenciesState.containsKey(dep.getName() + "=" + dep.getVersion())) {
                List<Dependency> collect = dependenciesState.values().stream()
                        .filter(d -> d.conflictsWith(dep.getName(), dep.getVersion()).size() > 0).collect(Collectors.toList());
                if (collect.size() > 0) invalidStateException();
                else install(new AddInstruction(dep.getName(), dep.getVersion()));
            }
        }
    }

    public void uninstall(RemoveInstruction instruction) throws Exception {
        Dependency dependency = getDependencyOfVersion(instruction.getVersion(), repository.getDependency(instruction.getName()));
        if (dependency != null && installed(dependency.name, dependency.version) && notConstraint(dependency.name, dependency.version)) {
            history.add(instruction);
            incUninstallRedundantDeps(dependency.deps);
        } else {
            throw new Exception("Invalid state. " + instruction.getName() + " is not installed.");
        }
    }

    private boolean notConstraint(String name, String version) {
        return constraints.get(name).stream().filter(c -> c.getVersion() == version).collect(Collectors.toList()).size() <= 0;
    }

    private void incUninstallRedundantDeps(List<Dependants> deps) throws Exception {
        for (int i = 0; i < deps.size(); i++) {
            Dependants dep = deps.get(i);
            if (installed(dep.getName(), dep.getVersion())) {
                List<Dependency> collect = dependenciesState.values().stream()
                        .filter(d -> d.requiredWith(dep.getName(), dep.getVersion()).size() > 0).collect(Collectors.toList());
                if (collect.size() > 0) invalidStateException();
                else uninstall(new RemoveInstruction(dep.getName(), dep.getVersion()));
            }
        }
    }

    public void writeHistory() throws IOException {
        FileWriter.create(workingDir + "commands.json").writeJSON(new JSONArray(stringFormat(history)));
    }

    private boolean installed(String name, String version) {
        return dependenciesState.containsKey(name + "=" + version);
    }

    private void processInstruction(Instruction instruction) throws Exception {
        if (dependenciesState.size() < 1) {
            instruction.run(this);
        } else {
            Collection<Dependency> curInstalls = dependenciesState.values();
            for(Dependency dependency : curInstalls) {
                List<Conflict> conflicts = dependency.conflictsWith(instruction.getName(), instruction.getVersion());
                if (conflicts.size() > 0) invalidStateException();
            }
            instruction.run(this);
        }
    }

    private void invalidStateException() throws Exception {
        throw new Exception("Can't reach intermediate state without conflict.");
    }

    private List<String> stringFormat(List<Instruction> history) {
        List<String> arr = new ArrayList<>();
        for(Instruction i : history) {
            String result = "";
            if (i.getClass() == AddInstruction.class) result += "+";
            else result += "-";
            arr.add(result + i.getName() + "=" + i.getVersion());
        }
        return arr;
    }

}
