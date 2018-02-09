import machine.State;
import model.Instruction;

import java.util.List;

import static util.Setup.getInstructions;
import static util.Setup.getMachine;

public class Main {

    public static void main(String[] args) throws Exception {
        String basePath = "../" + args[0].substring(0, args[0].lastIndexOf("/")) + "/";
        State machine = getMachine(basePath);
        start(machine, getInstructions(basePath, machine));
    }

    static void start(State machine, List<Instruction> instructions) throws Exception {
        machine.processInstructions(instructions);
        machine.writeHistory();
    }

}
