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
        List<Package> dependants = getDependents(p);
        if (dependants.isEmpty()) result.add(deletePackage(p));
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

    // note priorityQ... when I add anything I should add it to the priorityQ for this state
    // so I can quickly validate in removePackage if it will turn it into an InvalidState.
    public LinkedList<State> addPackage(Package p, PackageRepository packageRepository) throws Exception {
        LinkedList<State> result = new LinkedList<>();
        if (result.isEmpty()) {
            for (OptionalPackages d : p.dependants) {
                result.addAll(
                        d.getAllValid(this, packageRepository));
            }
        } else {
            for (int i = 0; i < result.size(); i++) {
                result.remove(i);
                for (OptionalPackages d : p.dependants) {
                    result.addAll(
                            d.getAllValid(result.get(i).clone(), packageRepository));
                }
            }
        }

        for (State s : result) {
            for(ConflictPackages cps : p.conflicts) {
                for(Package pk : cps.packages) {
                    if (isInstalled(pk)) {
                        //are dependent on it.
                        List<Package> cd = getDependents(pk); //todo
                        if (cd.isEmpty()) {
                            s.removePackage(pk, packageRepository);
                        } else if (canUninstall(cd)) {
                            s = satisfyPackageRemove(pk, cd);
                            s.removePackage(pk, packageRepository);
                        } // otherwise do nothing or return InvalidState.
                    }
                }
            }
        }
        priorityQ.add(p);
        history.add(new InstallInstruction(p.name, p.version, p.size));
        return result;
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
                pk.dependants.forEach(op -> op.forEach(pk2 -> {
                    if (pk2.name.equals(p.name)) result.add(pk);
                }));
            }
        return result;
    }

    //not sure if this is right, if im correct all dependents should
    //be inside the priorityQ so won't need to dig in and check them indiv.
    private boolean canUninstall(List<Package> pks) {
        for (Package up : pks) {
            for (Package p : priorityQ) {
                if (p.name.equals(up.name)) return false;
            }
        }
        return true;
    }

    private boolean isInstalled(Package pk) {
        for (Package p : packages) {
            if (p.name.equals(pk.name)) return true;
        }
        return false;
    }

    public State clone() {
        return new State(this.packages, this.history, this.priorityQ);
    }

    public boolean isValid() {
        return this instanceof Valid;
    }

    public boolean isFinal() {
        return this instanceof Final;
    }

}
