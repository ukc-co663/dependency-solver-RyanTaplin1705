package model;

import machine.State;

public class RemoveInstruction implements Instruction {

    private String name;
    private String version;

    public RemoveInstruction(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    @Override
    public void run(State state) throws Exception {
        state.uninstall(this);
    }

    public String getVersion() {
        return version;
    }
}
