package model.states;

import model.instructions.Instruction;
import model.instructions.RemoveInstruction;
import model.states.types.Final;
import model.states.types.Valid;
import repository.PackageRepository;
import repository.model.Package;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class State {

    private LinkedList<Package> priorityQ;
    public LinkedList<Instruction> history;
    public LinkedList<Package> packages;

    public State(LinkedList<Package> initialState) {
        this.packages = initialState;
        this.history = new LinkedList<>();
        this.priorityQ = new LinkedList<>();
    }

    public State(LinkedList<Package> initialState, LinkedList<Instruction> history, LinkedList<Package> priorityQ) {
        this.priorityQ = priorityQ;
        this.packages = initialState;
        this.history = history;
    }

    public LinkedList<State> removePackage(Package p, PackageRepository packageRepository) throws Exception {
        LinkedList<State> result = new LinkedList<>();
        List<Package> dependants = packageDepenants(p);
        if (dependants.isEmpty())  result.add(deletePackage(p));
        else {
            //for every dependent, clone this state and uninstall
            //after first iteration, pick up the previous set and process next dependent on them individually.
            //repeat until all dependants complete.
            for (Package d : dependants) {
                removePackage(d, packageRepository);
            }
        }
        return result;
    }

    private List<Package> packageDepenants(Package p) throws Exception {
        throw new Exception("TODO");
    }

    private State deletePackage(Package p) throws Exception {
        //if package exists in priortyQ then it is needed by a constraint.
        if (priorityQ.stream().filter(qP -> qP.name.equals(p.name)).collect(Collectors.toList()).size() > 0)
            return new InvalidState(this.packages, this.history, this.priorityQ);

        for (int i = 0; i < this.packages.size(); i++) {
            Package pp = this.packages.get(i);
            if (pp.name.equals(p.name)) {
                packages.remove(i);
                history.add(new RemoveInstruction(packages.get(i).name, packages.get(i).version));
                return new ValidState(this.packages, this.history, priorityQ);
            }
        }
        throw new Exception("The package you want to uninstall wasn't found.");
    }


    public LinkedList<State> addPackage(Package p, PackageRepository packageRepository) throws Exception {
        // as above, also note priorityQ... when I add anything I should add it to the priorityQ for this state
        // so I can quickly validate in removePackage if it will turn it into an InvalidState.

        LinkedList<State> result = processConflicts(p, 0,
                processDependant(p, 0, new LinkedList<>(), packageRepository), packageRepository);
//        history.add(new InstallInstruction(p.name, p.version, p.size));
//        packages.add(p);
        return result;
    }

    private LinkedList<State> processConflicts(Package p, int i, LinkedList<State> vss, PackageRepository packageRepository) throws Exception {
        if (i > vss.size() - 1) return vss;
        for (State vs : vss) {
            for (Package pp : p.conflicts.get(i)) {
                if (vs.canRemove(pp)) vs.removePackage(pp, packageRepository);
            }
        }
        return processConflicts(p, i+1, vss, packageRepository);
    }

    private LinkedList<State> processDependant(Package p, int i, LinkedList<State> vss, PackageRepository packageRepository) throws Exception {
        if (i > vss.size() - 1) return vss;
        for(State vs : vss) {
            for (Package pp : p.dependants.get(i)) {
                vs.addPackage(pp, packageRepository);
            }
        }
        return processDependant(p, i+1, vss, packageRepository);
    }

    public State clone() {
        return new State(this.packages, this.history, this.priorityQ);
    }

    public boolean canRemove(Package pp) throws Exception {
        throw new Exception("TODO");
    }

    public boolean isValid() {
        return this instanceof Valid;
    }

    public boolean isFinal() {
        return this instanceof Final;
    }

}
