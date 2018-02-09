package model;

import machine.State;

public class AddInstruction implements Instruction {

    private String name;
    private String version;

    public AddInstruction(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    @Override
    public void run(State state) throws Exception {
        state.install(this);
    }

    public String getVersion() {
        return version;
    }

}
