import model.Machine;
import model.instructions.Instruction;

import java.util.List;

import static util.Setup.getInstructions;
import static util.Setup.getMachine;

public class Main {

    public static void main(String[] args) throws Exception {
        Machine machine = getMachine(args[0], args[1], args[2]);
        start(machine, getInstructions(args[2], machine.getState()));
    }

    static void start(Machine machine, List<Instruction> instructions) throws Exception {
        machine.processInstructions(instructions);
        machine.printHistory();
    }

}
