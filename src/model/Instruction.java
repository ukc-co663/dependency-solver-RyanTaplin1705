package model;

import machine.State;

public interface Instruction {
    String getVersion();
    String getName();
    void run(State state) throws Exception;

    static Instruction create(String s) {
        if (s.charAt(0) == '+') {
            return new AddInstruction(s.split("=")[0], s.split("=")[1]);
        } else if (s.charAt(0) == '-') {
            return new RemoveInstruction(s.split("=")[0], s.split("=")[1]);
        } else {
            return new InvalidInstruction(s);
        }
    }
}
