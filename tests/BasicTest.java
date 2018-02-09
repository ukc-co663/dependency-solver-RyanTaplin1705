import machine.State;
import model.AddInstruction;
import model.Instruction;
import model.RemoveInstruction;
import org.junit.Before;
import org.junit.Test;
import util.Setup;

import java.util.List;

public class BasicTest{

    private State machine;
    private List<Instruction> instructions;

    @Before
    public void setUp() throws Exception {
        String basePath = "./resources/example-0/";
        machine = Setup.getMachine(basePath + "repository.json", basePath + "initial.json");
        instructions = Setup.getInstructions(basePath + "constraints.json");
    }

    @Test
    public void test() throws Exception {
        Main.start(machine, instructions);
        for (Instruction ins : machine.history) {
            System.out.println(writeInst(ins));
        }
    }

    private String writeInst(Instruction instruction) {
        String result = "";
        if (instruction.getClass() == AddInstruction.class) {
            result += "+";
        } else if (instruction.getClass() == RemoveInstruction.class) {
            result += "-";
        }
        return result + instruction.getName() + "=" + instruction.getVersion();
    }
}
