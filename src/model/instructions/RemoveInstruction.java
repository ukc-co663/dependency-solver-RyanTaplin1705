package model.instructions;

public class RemoveInstruction implements Instruction {

    public String name;
    public String version;

    public RemoveInstruction(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public int cost() {
        return 1000000;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getVersion() {
        return this.version;
    }
}
