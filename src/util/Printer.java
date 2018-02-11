package util;

import model.instructions.AddInstruction;
import model.instructions.Instruction;

import java.util.ArrayList;
import java.util.List;

public class Printer {

    public static List<String> stringFormat(List<Instruction> history) {
        List<String> arr = new ArrayList<>();
        for(Instruction instruction : history) {
            String result = instructionPrefix(instruction);
            arr.add(result + instruction.getName() + "=" + instruction.getVersion());
        }
        return arr;
    }

    private static String instructionPrefix(Instruction aClass) {
        if (aClass.getClass() == AddInstruction.class) return  "+";
        else return "-";
    }
}
