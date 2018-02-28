package model.states;

import model.instructions.Instruction;
import model.states.types.Invalid;
import repository.model.Package;

import java.util.LinkedList;

public class InvalidState extends State implements Invalid {

    public InvalidState(LinkedList<Package> packages, LinkedList<Instruction> history, LinkedList<Package> priorityQ) throws Exception {
        super(packages, history, priorityQ);
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public boolean isFinal() {
        return false;
    }
}
