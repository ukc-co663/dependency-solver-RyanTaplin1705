package model.states;

import model.instructions.InstallInstruction;
import model.instructions.Instruction;
import model.instructions.RemoveInstruction;
import model.states.types.Final;
import model.states.types.Valid;
import repository.ConflictPackages;
import repository.OptionalPackages;
import repository.PackageRepository;
import repository.model.Package;

import java.util.LinkedList;
import java.util.List;

public class State {

    public LinkedList<Package> priorityQ;
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

    public LinkedList<ValidState> removePackage(Package p, PackageRepository packageRepository) throws Exception {
        LinkedList<ValidState> results = new LinkedList<>();
        LinkedList<OptionalPackages> deps = getDeps(p);
        if (!deps.isEmpty()) {
            while(!deps.isEmpty()) {
                OptionalPackages op = deps.pollLast();
                for (Package pk : op.packages) {
                    if (results.isEmpty()) {
                        if (this.clone().deletePackage(pk) instanceof Valid) results.add((ValidState)this.clone().deletePackage(pk));
                    } else {
                        LinkedList<ValidState> tr = new LinkedList<>();
                        for (int i = 0; i < results.size(); i++) {
                            if (this.clone().deletePackage(pk) instanceof Valid) tr.add((ValidState)this.clone().deletePackage(pk));
                        }
                        results = tr;
                    }
                }
            }
            for (State state : results) state.deletePackage(p);
        } else {
            if (this.clone().deletePackage(p) instanceof Valid)
                results.add((ValidState)this.clone().deletePackage(p));
        }
        return results;
    }

    public LinkedList<State> removePackage2(Package p, PackageRepository packageRepository) throws Exception {
        LinkedList<State> result = new LinkedList<>();
        List<Package> dependants = getDependents(p);
        if (dependants.isEmpty()) result.add(deletePackage(p));
        else {
            //for every dependent, clone this state and uninstall
            //after first iteration, pick up the previous set and process next dependent on them individually.
            //repeat until all dependants complete.
            LinkedList<State> tr = new LinkedList<>();
            for (Package d : dependants) {
                if (tr.isEmpty()) tr.addAll(removePackage(d, packageRepository));
                else {
                    for (State s : tr) {
                        s.removePackage(d, packageRepository);
                    }
                }
            }
            result = tr;
        }
        for (State s : result) s.removePackage(p, packageRepository);
        return result;
    }

    private boolean violatesConstraint(Package p) {
        for (Package pQ : priorityQ) {
            if (pQ.name.equals(p.name)) return true;
        }
        return false;
    }

    private State deletePackage(Package p) throws Exception {
        //if package exists in priortyQ then it is needed by a constraint.
        if (violatesConstraint(p)) return new InvalidState(this.packages, this.history, this.priorityQ);

        if (!isInstalled(p)) return this.cloneValid();
        LinkedList<Package> tp = new LinkedList<>(this.packages);
        for (int i = 0; i < tp.size(); i++) {
            if (tp.get(i).name.equals(p.name) && tp.get(i).version.equals(p.version)) {
                tp.remove(i);
                history.add(new RemoveInstruction(p.name, p.version));
                return new ValidState(tp, this.history, this.priorityQ);
            }
        }
        return this;
    }

    // note priorityQ... when I add anything I should add it to the priorityQ for this state
    // so I can quickly validate in removePackage if it will turn it into an InvalidState.

    public LinkedList<OptionalPackages> getDeps(Package p) throws Exception {
        if (p.dependants.isEmpty()) return new LinkedList<>();
        else {
            LinkedList<OptionalPackages> processed = new LinkedList<>();
            LinkedList<OptionalPackages> unprocessed = new LinkedList<>();
            unprocessed.addAll(p.dependants);
            while (!unprocessed.isEmpty()) {
                OptionalPackages op = unprocessed.pollFirst();
                processed.add(op);
                for (Package pk : op.packages) {
                    if (!pk.dependants.isEmpty()) {
                        for (OptionalPackages op2 : pk.dependants) {
                            if (!processed.contains(op2)) unprocessed.add(op2);
                        }
                    }
                }
            }
            return processed;
        }
    }

    public LinkedList<ValidState> addPackage(Package p, PackageRepository packageRepository) throws Exception {
        LinkedList<ValidState> results = new LinkedList<>();
        for (OptionalPackages d : p.dependants) {
            //A -> B or C
            if (results.isEmpty()) {
                for (Package p2 : d.packages) {
                    results.addAll(this.clone().addPackage(p2, packageRepository));
                }
            } else {
                LinkedList<ValidState> tr = new LinkedList<>();
                for (int i = 0; i < results.size(); i++) {
                    for (Package p2 : d.packages) {
                        tr.addAll(results.get(i).clone().addPackage(p2, packageRepository));
                    }
                }
                results = tr;
            }
        }


        if (results.isEmpty()) {
            LinkedList<ValidState> clones = this.clone().removeConflicts(p, packageRepository);
            for (ValidState cs : clones) {
                cs.addUpdate(p);
            }
            results = clones;
        } else {
            LinkedList<ValidState> re = new LinkedList<>();
            for (State s : results) {
                for (State cs : s.removeConflicts(p, packageRepository)) {
                    State state = cs.addUpdate(p);
                    if (state instanceof  Valid) re.add((ValidState)state);
                }
            }
            results = re;
        }
        return results;
    }

    private LinkedList<ValidState> removeConflicts(Package p, PackageRepository packageRepository) throws Exception {
        LinkedList<ValidState> results = new LinkedList<>();
        for(ConflictPackages cps : p.conflicts) {
            for(Package pk : cps.packages) {
                if (isInstalled(pk)) {
                    //are dependent on it.
                    List<Package> cd = getDependents(pk);
                    if (cd.isEmpty()) {
                        if (results.isEmpty()) results.addAll(this.clone().removePackage(pk, packageRepository));
                        else {
                            LinkedList<ValidState> ts = new LinkedList<>();
                            for (State s : results) {
                                ts.addAll(s.removePackage(pk, packageRepository));
                            }
                            results = ts;
                        }
                    } else if (canUninstall(cd)) {
                        if (results.isEmpty()) {
                            State s = this.clone().satisfyPackageRemove(pk, cd);
                            results.addAll(s.removePackage(pk, packageRepository));
                        } else {
                            LinkedList<ValidState> ts = new LinkedList<>();
                            for (State s : results) {
                                State state = s.satisfyPackageRemove(pk, cd);
                                ts.addAll(state.removePackage(pk, packageRepository));
                            }
                            results = ts;
                        }

                    } // otherwise do nothing or return InvalidState.
                }
            }
        }
        if (results.isEmpty()) results.add(this.cloneValid());
        return results;
    }

    public State addUpdate(Package p) throws Exception {
        if (isInstalled(p)) return new ValidState(this.packages, this.history, this.priorityQ);

        boolean validate = validate(p);
        this.packages.add(p);
        this.priorityQ.add(p);
        this.history.add(new InstallInstruction(p.name, p.version, p.size));

        if (validate) return new ValidState(this.packages, this.history, this.priorityQ);
        else return new InvalidState(this.packages, this.history, this.priorityQ);
    }

    private boolean validate(Package p) {
        for (Package p2 : priorityQ) {
            if (p.name.equals(p2.name)) return false;
        }
        return true;
    }

    //When you need to remove a package
    //but it has dependants that need removing first.
    private State satisfyPackageRemove(Package pk, List<Package> cps) throws Exception {
        for (int i = 0; i < packages.size(); i++) {
            Package p = packages.get(i);
              for (Package cp : cps) {
                  if (p.name.equals(cp.name)) {
                      LinkedList<Package> cd = getDependents(p);
                      if (!cd.isEmpty()) {
                          satisfyPackageRemove(p, cd);
                      } else deletePackage(p);
                  }
              }
        }
        deletePackage(pk);
        return this.clone();
    }

    private LinkedList<Package> getDependents(Package p) {
        LinkedList<Package> result = new LinkedList<>();
            for (Package pk : packages) {
                pk.dependants.forEach(op -> op.packages.forEach(pk2 -> {
                    if (pk2.name.equals(p.name)) result.add(pk);
                }));
            }
        return result;
    }

    //not sure if this is right, if im correct all dependents should
    //be inside the priorityQ so won't need to dig in and check them indiv.
    public boolean canUninstall(List<Package> pks) {
        for (Package up : pks) {
            for (Package p : priorityQ) {
                if (p.name.equals(up.name)) return false;
            }
        }
        return true;
    }

    public boolean isInstalled(Package pk) {
        for (Package p : packages) {
            if (p.name.equals(pk.name)) return true;
        }
        return false;
    }

    public State clone() {
        return new State(
                new LinkedList<>(this.packages),
                new LinkedList<>(this.history),
                new LinkedList<>(this.priorityQ));
    }

    public ValidState cloneValid() throws Exception {
        return new ValidState(
                new LinkedList<>(this.packages),
                new LinkedList<>(this.history),
                new LinkedList<>(this.priorityQ));

    }

    public boolean isValid() {
        return this instanceof Valid;
    }

    public boolean isFinal() {
        return this instanceof Final;
    }
}
