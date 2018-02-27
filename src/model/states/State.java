package model.states;

import model.instructions.Instruction;
import repository.model.Package;

import java.util.LinkedList;

public class State {

    public LinkedList<Instruction> history = new LinkedList<>();
    public LinkedList<Package> packages;

    public State(LinkedList<Package> initialState) throws Exception {
        this.packages = initialState;
    }

    public State removePackage(Package p) {
        //history.add(p);
        packages.remove(p);
        return this;
    }

    public State addPackage(Package p) {
        //history.add(p);
        packages.add(p);
        return this;
    }

    public VirtualState virtualize() throws Exception {
        return new VirtualState(this.packages);
    }
}
