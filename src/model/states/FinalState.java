package model.states;

import model.constraints.ForbiddenConstraint;
import model.instructions.Instruction;
import model.states.types.Final;
import model.states.types.Valid;
import org.json.JSONArray;
import repository.model.Package;

import java.util.LinkedList;
import java.util.List;

import static util.Printer.stringFormat;

public class ValidFinalState extends State implements Final, Valid {

    public ValidFinalState(LinkedList<Package> packages, LinkedList<Instruction> history) throws Exception {
        super(packages, history);
    }

    public int cost() {
        int total = 0;
        for (Instruction i : history) {
            total += i.cost();
        }
        return total;
    }

    public void printResults() throws Exception {
        System.out.println(new JSONArray(stringFormat(history)));
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean isFinal() {
        return true;
    }

    @Override
    public List<Final> inspection(List<ForbiddenConstraint> inspectors) throws Exception {
        throw new Exception("Shouldn't be inspecting final states...");
    }
}
