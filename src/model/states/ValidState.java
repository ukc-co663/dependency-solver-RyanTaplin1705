package model.states;

import model.instructions.Instruction;
import org.json.JSONArray;
import repository.model.Package;

import java.util.LinkedList;

import static util.Printer.stringFormat;

public class ValidState extends State {

    public ValidState(LinkedList<Package> packages, LinkedList<Instruction> history) throws Exception {
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
}
