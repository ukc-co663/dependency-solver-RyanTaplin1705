package model;

import machine.State;
import repository.model.Dependency;
import repository.model.Operation;

import java.util.List;
import java.util.stream.Collectors;

import static repository.model.Operation.NONE;
import static repository.model.Operation.extract;
import static util.VersionChecker.versionEvaluate;

public interface Instruction {
    String getVersion();
    String getName();
    void run(State state) throws Exception;

    static List<Instruction> create(String s, State machine) throws Exception {
        Action action = Action.convert(s.charAt(0));
        String rest = s.substring(1, s.length());
        Operation op = extract(rest);

        String name = rest.substring(0, rest.indexOf(op.getStringValue()));

        String version = op.equals(NONE) ? null :  rest.substring(rest.indexOf(op.getStringValue()), rest.length());
        return getDeps(name, version, op, action, machine).stream()
                .map(dep -> createInstructionType(action, name, version)).collect(Collectors.toList());

    }

    static List<Dependency> getDeps(String name, String version, Operation op, Action action, State machine) throws Exception {
        switch (op) {
            case NONE:
                return machine.repository.getDependency(name);
            case LESS_THAN:
            case GREATER_THAN:
            case LESS_THAN_OR_EQUAL_TO:
            case GREATER_THAN_OR_EQUAL_TO:
            case EQUAL_TO:
                return machine.repository.getDependency(name).stream()
                        .filter(dep -> versionEvaluate(dep.version, version, op)).collect(Collectors.toList());
            default:
                throw new Exception("Operation " + op.getStringValue() + " is not mapped or does not exist.");
        }
    }

    static Instruction createInstructionType(Action action, String name, String version) {
        switch(action) {
            case INSTALL:
                return new AddInstruction(name, version);
            case UNINSTALL:
                return new RemoveInstruction(name, version);
            default:
                return new InvalidInstruction(action.toString() + name + "?" + version);
        }
    }

}
