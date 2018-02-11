package repository.model;

import model.Operation;

import java.util.ArrayList;
import java.util.List;

public class Conflict {

    private String name;
    private String version;
    private Operation operation;

    public Conflict(String name, String version, Operation operation) {
        this.name = name;
        this.version = version;
        this.operation = operation;
    }

    public boolean isConflicting(String version) {
        String[] cSeg = this.version.split(".");
        String[] vSeg = version.split(".");

        List<Boolean> bo = new ArrayList<>();
        for(int i = 0; i < Math.min(cSeg.length, vSeg.length); i++) {
            bo.add(this.operation.evaluate(
                        Integer.parseInt(cSeg[i]),
                        Integer.parseInt(vSeg[i])));
        }
        return bo.contains(true);
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public Operation getOperation() {
        return operation;
    }
}
