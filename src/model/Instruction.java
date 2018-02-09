package model;

import machine.State;
import repository.model.Dependency;

import java.util.LinkedList;

public interface Instruction {
    String getVersion();
    String getName();
    void run(State state) throws Exception;

    static Instruction create(String s, State machine) {
        String name;
        String version = null;
        char op = s.charAt(0);

        if(s.contains("=")) {
            name = s.substring(1, s.indexOf('='));
            version = s.substring(s.indexOf('=', s.length()));
        } else {
            name = s.substring(1, s.length());

            LinkedList<Dependency> dependency1 = machine.repository.getDependency(name);
            for (int i = 0; i < dependency1.size(); i++) {
                boolean conflict = false;
                for (Dependency dependency : machine.dependenciesState.values()) {
                    if (dependency.conflictsWith(dependency1.get(i)).size() > 0) {
                        conflict = true;
                    }
                }
                if (!conflict) {
                    version = dependency1.get(i).version;
                    break;
                }
            }
        }

        if (op == '+' || op == '-') return Instruction.process(op, name, version);
        else return new InvalidInstruction(s);
    }

    static Instruction process(char op, String name, String version) {
        if (op == '+') return new AddInstruction(name, version);
        else return new RemoveInstruction(name, version);
    }

}
