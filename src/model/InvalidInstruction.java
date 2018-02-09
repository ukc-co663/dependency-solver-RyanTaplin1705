package model;

import machine.State;

public class InvalidInstruction implements Instruction {

    private String raw;

    public InvalidInstruction(String rawInstruction) {
        this.raw = rawInstruction;
    }

    @Override
    public String getVersion() {
        return "";
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void run(State state) {
        System.out.println("This instruction had an error. Raw value: {" + this.raw + "}");
    }


}
