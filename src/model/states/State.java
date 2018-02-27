package model.states;

import model.instructions.InstallInstruction;
import model.instructions.Instruction;
import model.instructions.RemoveInstruction;
import repository.model.Package;

import java.util.LinkedList;
import java.util.List;

public class State {

    public LinkedList<Instruction> history = new LinkedList<>();
    public LinkedList<Package> packages;

    public State(LinkedList<Package> initialState) {
        this.packages = initialState;
        this.history = new LinkedList<>();
    }

    public State(LinkedList<Package> initialState, LinkedList<Instruction> history) {
        this.packages = initialState;
        this.history = history;
    }

    public LinkedList<State> removePackage(Package p) throws Exception {
        LinkedList<State> result = new LinkedList<>();
        for (int i = 0; i < packages.size(); i++) {
            Package pp = packages.get(i);
            if (pp.name.equals(p.name)) {
                // find any packages that depend on this package and check if can needed/ can be removed...
                packages.remove(i);
                history.add(new RemoveInstruction(packages.get(i).name, packages.get(i).version));
                break;
            }
        }
        return result;
    }

    public LinkedList<State> addPackage(Package p) throws Exception {
        LinkedList<State> result = processConflicts(p, 0,
                processDependant(p, 0, new LinkedList<>()));
        history.add(new InstallInstruction(p.name, p.version, p.size));
        packages.add(p);
        return result;
    }

    private LinkedList<State> processConflicts(Package p, int i, LinkedList<State> vss) throws Exception {
        if (i > vss.size() - 1) return vss;
        for (State vs : vss) {
            for (Package pp : p.conflicts.get(i)) {
                if (vs.canRemove(pp)) vs.removePackage(pp);
            }
        }
        return processConflicts(p, i+1, vss);
    }

    private LinkedList<State> processDependant(Package p, int i, LinkedList<State> vss) throws Exception {
        if (i > vss.size() - 1) return vss;
        for(State vs : vss) {
            for (Package pp : p.dependants.get(i)) {
                vs.addPackage(pp);
            }
        }
        return processDependant(p, i+1, vss);
    }

    public State clone() {
        return new State(this.packages, this.history);
    }

    public List<Package> getConflicts(Package p) throws Exception {
        throw new Exception("TODO");
    }

    public boolean canRemove(Package pp) throws Exception {
        throw new Exception("TODO");
    }


}
