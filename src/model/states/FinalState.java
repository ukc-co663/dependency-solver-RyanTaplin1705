package model.states;

import model.instructions.Instruction;
import model.states.types.Final;
import model.states.types.Valid;
import org.json.JSONArray;
import repository.model.Package;

import java.util.LinkedList;

import static util.Printer.stringFormat;

public class FinalState extends State implements Final {

    public FinalState(LinkedList<Package> packages, LinkedList<Instruction> history, LinkedList<Package> priorityQ) throws Exception {
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
        return this instanceof Valid;
    }

    @Override
    public boolean isFinal() {
        return true;
    }

    @Override
    public void printResults() throws Exception {
        System.out.println(new JSONArray(stringFormat(history)));
    }
}
