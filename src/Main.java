import machine.State;
import model.Instruction;

import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        throw new Exception(args[0]);
//        String basePath = args[0].substring(0, args[0].lastIndexOf("\\"));
//        State machine = getMachine(basePath);
//        start(machine, getInstructions(basePath, machine));
    }

    public static void start(State machine, List<Instruction> instructions) throws Exception {
        machine.processInstructions(instructions);
        machine.writeHistory();
    }

}
