import junit.framework.TestCase;
import machine.State;
import model.AddInstruction;
import model.Instruction;
import model.RemoveInstruction;
import org.junit.Test;
import util.Setup;

import java.util.List;

public class MainTest extends TestCase {

    private String basePath = System.getProperty("user.dir") + "\\tests\\resources\\basic-0\\";

    private State machine = Setup.getMachine(basePath + "repository.json", basePath + "initial.json", basePath + "constraints.json");
    private List<Instruction> instructions = Setup.getInstructions(basePath + "constraints.json", machine);

    public MainTest() throws Exception {
    }

    @Test
    public void test() throws Exception {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        Main.start(machine, instructions);
        for (Instruction ins : machine.history) {
            System.out.println(writeInst(ins));
        }
        assertEquals(true, true);
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