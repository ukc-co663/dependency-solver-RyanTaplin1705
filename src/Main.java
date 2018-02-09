import machine.State;
import model.Instruction;

import java.util.List;

import static util.Setup.getInstructions;
import static util.Setup.getMachine;

public class Main {


    public static void main(String[] args) throws Exception {
        start(getMachine(args[0], args[1]), getInstructions(args[2]));
    }

    public static void start(State machine, List<Instruction> instructions) throws Exception {
        machine.processInstructions(instructions);
        machine.writeHistory();
    }

}
