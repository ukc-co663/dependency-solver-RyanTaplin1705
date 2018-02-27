package model.states;

import model.instructions.Instruction;
import org.json.JSONArray;
import repository.model.Package;

import java.util.LinkedList;

import static util.Printer.stringFormat;

public class ValidState  {

    public final LinkedList<Package> packages;
    public final LinkedList<Instruction> history;

    public ValidState(LinkedList<Package> packages, LinkedList<Instruction> history) throws Exception {
        this.packages = packages;
        this.history = history;
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
}
