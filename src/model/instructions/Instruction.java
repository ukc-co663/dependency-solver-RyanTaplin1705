package model.instructions;

import model.Action;
import model.Operation;
import model.State;
import repository.model.Dependency;

import java.util.List;
import java.util.stream.Collectors;

import static model.Operation.*;
import static util.VersionChecker.versionEvaluate;

public interface Instruction {
    String getVersion();
    String getName();
    void run(State state) throws Exception;

    static List<Instruction> create(String s, State machine) throws Exception {
        Action action = Action.convert(s.charAt(0));
        String rest = s.substring(1, s.length());
        Operation op = extractOperator(rest);

        String name = rest.substring(0, op == Operation.NONE ? rest.length() : rest.indexOf(op.getStringValue()));

        String version = op.equals(NONE) ? null :  rest.substring(op == GREATER_THAN_OR_EQUAL_TO || op == LESS_THAN_OR_EQUAL_TO ? 0 : 1 + rest.indexOf(op.getStringValue()), rest.length());
        return getDeps(name, version, op, action, machine).stream()
                .map(dep -> createInstructionType(action, dep.name, dep.version)).collect(Collectors.toList());

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
                return null; //risky, should throw InvalidParsingException but map doesn't handle it.
        }
    }

}
