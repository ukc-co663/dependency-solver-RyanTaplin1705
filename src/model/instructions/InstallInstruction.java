package model.instructions;

public class InstallInstruction implements Instruction {

    public String name;
    public String version;
    public int size;

    public InstallInstruction(String name, String version, int size) {
        this.name = name;
        this.version = version;
        this.size = size;
    }

    public int cost() {
        return size;
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
