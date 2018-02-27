package model.states;

import model.instructions.InstallInstruction;
import model.instructions.Instruction;
import model.instructions.RemoveInstruction;
import repository.model.Package;

import java.util.LinkedList;
import java.util.List;

public class VirtualState {

    public LinkedList<Package> packages;
    public LinkedList<Instruction> history;

    public VirtualState(LinkedList<Package> packages) throws Exception {
        this.packages = packages;
    }

    public static VirtualState create(LinkedList<Package> packages) throws Exception {
        return new VirtualState(packages);
    }

    public ValidState complete() throws Exception {
        return new ValidState(this.packages, this.history);
    }

    public LinkedList<VirtualState> removePackage(Package p) throws Exception {
        LinkedList<VirtualState> result = new LinkedList<>();
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

    public LinkedList<VirtualState> addPackage(Package p) throws Exception {
        LinkedList<VirtualState> result = new LinkedList<>();
        processDependant(p, 0);

        for (int i = 0; i < p.dependants.size(); i++) {
            if (p.dependants.get(i).size() > 1) {
                for (Package pp : p.dependants.get(i)) {
                    result.addAll(VirtualState.create(this.packages).addPackage(pp));
                }
            }
        }
        for (int i = 0; i < p.conflicts.size(); i++) {
            //TODO:: check if this contains works - or replace more appropriately.
            if (p.conflicts.get(i).size() > 1) {
                for (Package pp : p.conflicts.get(i)) {
                    result.addAll(VirtualState.create(this.packages).removePackage(pp));
                }
            }
        }
        history.add(new InstallInstruction(p.name, p.version, p.size));
        packages.add(p);
        return result;
    }

    private LinkedList<VirtualState> processDependant(Package p, int i) throws Exception {
        LinkedList<VirtualState> result = new LinkedList<>();
        if (p.dependants.get(i).size() > 1) {
            for(Package pp : p.dependants.get(i)) {
                result.addAll(VirtualState.create(this.packages).addPackage(pp));
            }
        }
        return processDependant2(result, p, i+1);
    }

    private LinkedList<VirtualState> processDependant2(LinkedList<VirtualState> vss, Package p, int i) throws Exception {
        if (i > vss.size() - 1) return vss;
        LinkedList<VirtualState> result = new LinkedList<>();
        for(VirtualState vs : vss) {
            for (Package pp : p.dependants.get(i)) {
                vs.addPackage(pp);
            }
        }
        return processDependant2(result, p, i+1);
    }

    public List<Package> getConflicts(Package p) throws Exception {
        throw new Exception("TODO");
    }
}
