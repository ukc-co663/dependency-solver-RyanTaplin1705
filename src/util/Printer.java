package util;

import model.instructions.InstallInstruction;
import model.instructions.Instruction;
import model.instructions.RemoveInstruction;

import java.util.ArrayList;
import java.util.List;

public class Printer {

    public static List<String> stringFormat(List<Instruction> history) throws Exception {
        List<String> arr = new ArrayList<>();
        for(Instruction instruction : history) {
            String result = instructionPrefix(instruction);
            arr.add(result + instruction.getName() + "=" + instruction.getVersion());
        }
        return arr;
    }

    private static String instructionPrefix(Instruction c) throws Exception {
        if (c instanceof InstallInstruction) return  "+";
        else if (c instanceof RemoveInstruction) return "-";
        else throw new Exception("Invalid instruction type. Please verify.");
    }
}
