import machine.State;
import model.AddInstruction;
import model.Instruction;
import model.InvalidInstruction;
import model.RemoveInstruction;
import org.json.JSONArray;
import repository.DependencyRepository;
import repository.model.Conflict;
import repository.model.Dependency;
import sun.plugin.dom.exception.InvalidStateException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static machine.State.isValid;
import static repository.model.Operation.*;
import static util.FileReader.readFile;
import static util.Setup.getInitialState;
import static util.Setup.getRepository;

public class Main {

    public static State machine;

    public static void main(String[] args) throws Exception {
        DependencyRepository repository = new DependencyRepository(getRepository(args[0]));
        HashMap<String, Dependency> initialState = getInitialState(args[1], repository);

        JSONArray array = new JSONArray(readFile(args[2]));
        List<Instruction> instructions = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Instruction instruction = makeInstruction(array.getString(i));
            instructions.add(instruction);
        }

         if (isValid(initialState))    /* TODO: is validation of initial state necessary? */
            machine = new State(repository, initialState);
         else
             throw new InvalidStateException("Initial state is not valid.");

        writeCommands(new JSONArray(processInstructions(instructions)));
    }

    private static List<Instruction> processInstructions(List<Instruction> instructions) throws Exception {
        ArrayList<Instruction> commands = new ArrayList<>();
        HashMap<String, String> constraints = new HashMap<>();
        for(int i = 0; i < instructions.size(); i++) {
            Instruction instruction = instructions.get(i);
            commands.addAll(processInstruction(instruction, constraints));
            constraints.put(instruction.getName(), instruction.getVersion());
        }
        return commands;
    }

    private static List<Instruction> processInstruction(Instruction instruction,  HashMap<String, String> constraints) throws Exception {
        ArrayList<Instruction> commands = new ArrayList<>();
        if (machine.dependenciesState.size() < 1) {
            commands.add(instruction);
            instruction.run(machine);
        } else {
            Collection<Dependency> curInstalls = machine.dependenciesState.values();
            for(Dependency dependency : curInstalls) {
                List<Conflict> conflicts = getDependencyConflicts(dependency, instruction);
                if (conflicts.size() > 0) {
                    commands.addAll(resolveConflicts(conflicts, instruction));
                }
                else commands.add(instruction);
            }
        }
        return commands;
    }

    private static List<Instruction> resolveConflicts(List<Conflict> conflicts, Instruction instruction) throws Exception {
        List<Instruction> commands = new ArrayList<>();
        for(int i = 0; i < conflicts.size(); i++) {
            Conflict conflict = conflicts.get(i);
            if (conflict.operation == GREATER_THAN || conflict.operation == GREATER_THAN_OR_EQUAL_TO) {
                if (versionGreaterThan(conflict.version, instruction.getVersion())) {
                    RemoveInstruction removeInstruction = new RemoveInstruction(conflict.name, conflict.version);
                    machine.uninstall(removeInstruction);
                    commands.add(removeInstruction);

                    String downgradedVersion = getDowngradedVersion(conflict);
                    AddInstruction addInstruction = new AddInstruction(conflict.name, downgradedVersion);
                    machine.install(addInstruction);
                    commands.add(addInstruction);
                }
            } else if (conflict.operation == LESS_THAN || conflict.operation == LESS_THAN_OR_EQUAL_TO) {
                if (!versionGreaterThan(conflict.version, instruction.getVersion())) {
                    RemoveInstruction removeInstruction = new RemoveInstruction(conflict.name, conflict.version);
                    machine.uninstall(removeInstruction);
                    commands.add(removeInstruction);

                    String upgradedVersion = getUpgradedVersion(conflict);
                    AddInstruction addInstruction = new AddInstruction(conflict.name, upgradedVersion);
                    machine.install(addInstruction);
                    commands.add(addInstruction);
                }
            }
        }
        return commands;
    }

    private static String getUpgradedVersion(Conflict conflict) throws Exception {
        LinkedList<Dependency> dependencies = machine.repository.getDependency(conflict.name);
        for (int i = 0; i > dependencies.size(); i++) {
            Dependency dependency = dependencies.get(i);
            if (versionGreaterThan(conflict.version, dependency.version) && getAllConflicts(dependency).size() <= 0)
                return dependency.version;
        }
        throw new Exception("This package can't be upgraded.");
    }

    private static String getDowngradedVersion(Conflict conflict) throws Exception {
        LinkedList<Dependency> dependencies = machine.repository.getDependency(conflict.name);
        for (int i = dependencies.size(); i > 0; i--) {
            Dependency dependency = dependencies.get(i);
            if (!versionGreaterThan(conflict.version, dependency.version) && getAllConflicts(dependency).size() <= 0)
                return dependency.version;
        }
        throw new Exception("This package can't be downgraded.");
    }

    private static boolean versionGreaterThan(String resolve, String comp) {
        String[] resolveS = resolve.split(".");
        String[] compS = comp.split(".");

        int itLength = Math.min(compS.length, resolveS.length);
        for (int i = 0; i < itLength; i++) {
            if (Integer.parseInt(resolveS[i]) > Integer.parseInt(compS[i])) return true;
        }
        return false;
    }

    private static List<Conflict> getAllConflicts(Dependency dep) {
        List<Conflict> conflicts = new ArrayList<>();
        Collection<Dependency> curInstalls = machine.dependenciesState.values();
        for(Dependency dependency : curInstalls) {
            conflicts.addAll(getDependencyConflicts(dependency, dep));
        }
        return conflicts;
    }

    private static List<Conflict> getDependencyConflicts(Dependency dependency, Dependency dependency2) {
        return dependency.conflicts.stream().map(conflict -> {
            if (conflict.name.equals(dependency2.name) &&
                    conflict.isConflicting(dependency2.version)) return conflict;
            else return null;
        }).collect(Collectors.toList());
    }

    private static List<Conflict> getDependencyConflicts(Dependency dependency, Instruction instruction) {
        return dependency.conflicts.stream().map(conflict -> {
            if (conflict.name.equals(instruction.getName()) &&
                    conflict.isConflicting(instruction.getVersion())) return conflict;
            else return null;
        }).collect(Collectors.toList());
    }

    private static boolean hasConflicts(Dependency dependency, Instruction instruction) {
        for(Conflict conflict : dependency.conflicts) {
            if (conflict.name.equals(instruction.getName()) &&
                    conflict.isConflicting(instruction.getVersion()))
                return true;
        }
        return false;
    }

    private static void writeCommands(JSONArray arr) {
        try {
            FileWriter fileWriter = new FileWriter("commands.json");
            fileWriter.write(arr.toString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Instruction makeInstruction(String s) {
        if (s.charAt(0) == '+') {
            return new AddInstruction(s.split("=")[0], s.split("=")[1]);
        } else if (s.charAt(0) == '-') {
            return new RemoveInstruction(s.split("=")[0], s.split("=")[1]);
        } else {
            return new InvalidInstruction(s);
        }
    }
}
