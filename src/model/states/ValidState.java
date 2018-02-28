package model.states;

import model.constraints.ForbiddenConstraint;
import model.instructions.Instruction;
import model.states.types.Valid;
import repository.PackageRepository;
import repository.model.Package;

import java.util.LinkedList;
import java.util.List;

public class ValidState extends State implements Valid {

    public ValidState(LinkedList<Package> packages, LinkedList<Instruction> history, LinkedList<Package> priorityQ) throws Exception {
        super(packages, history, priorityQ);
    }

    public int cost() {
        int total = 0;
        for (Instruction i : history) {
            total += i.cost();
        }
        return total;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public List<FinalState> inspection(List<ForbiddenConstraint> inspectors, PackageRepository packageRepository) throws Exception {
        throw new Exception("TODO");
//        return new ValidFinalState(null, null);
    }
}
