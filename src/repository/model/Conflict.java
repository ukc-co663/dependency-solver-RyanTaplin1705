package repository.model;

import java.util.ArrayList;
import java.util.List;

public class Conflict {

    public String name;
    public String version;
    public Operation operation;

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

    public String resolve() {
        String[] curV = this.version.split(".");

        if (this.operation == Operation.LESS_THAN) {
            curV[curV.length] = (Integer.parseInt(curV[curV.length]) - 1) + "";
        } else if (this.operation == Operation.GREATER_THAN) { //check if this version exists?
            curV[curV.length] = (Integer.parseInt(curV[curV.length]) + 1) + "";
        }

        StringBuilder sb = new StringBuilder();
        for (String s : curV) {
            sb.append(s + ",");
        }
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }
}
