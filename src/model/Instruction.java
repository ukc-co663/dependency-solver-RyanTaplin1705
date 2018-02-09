package model;

import machine.State;

public interface Instruction {
    String getVersion();
    String getName();
    void run(State state) throws Exception;
}
