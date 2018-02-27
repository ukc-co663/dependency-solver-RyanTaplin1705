package model.states;

import model.instructions.Instruction;
import repository.model.Package;

import java.util.LinkedList;

public class InvalidState extends State {

    public InvalidState(LinkedList<Package> packages, LinkedList<Instruction> history) throws Exception {
        super(packages, history);
    }
}
