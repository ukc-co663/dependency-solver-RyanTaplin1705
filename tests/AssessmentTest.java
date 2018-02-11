import junit.framework.TestCase;
import machine.State;
import model.Instruction;
import org.junit.Test;
import util.Setup;

import java.util.List;

public class AssessmentTest extends TestCase {

    private final String basePath = System.getProperty("user.dir") + "\\tests\\resources\\assessment";

    public void runTests(int testNum) throws Exception {
        String fullPath = basePath + "\\seen-" + testNum + "\\";
        State machine = Setup.getMachine(fullPath  + "repository.json", fullPath + "initial.json", fullPath + "constraints.json");
        List<Instruction> instructions = Setup.getInstructions(fullPath + "constraints.json", machine);
        Main.start(machine, instructions);
    }

    @Test
    public void testZero() throws Exception {
        runTests(0);
        assertEquals(true, true);
    }

    @Test
    public void testOne() throws Exception {
        runTests(1);
        assertEquals(true, true);
    }

    @Test
    public void testTwo() throws Exception {
        runTests(2);
        assertEquals(true, true);
    }

    @Test
    public void testThree() throws Exception {
        runTests(3);
        assertEquals(true, true);
    }

    @Test
    public void testFour() throws Exception {
        runTests(4);
        assertEquals(true, true);
    }

    @Test
    public void testFive() throws Exception {
        runTests(5);
        assertEquals(true, true);
    }

    @Test
    public void testSix() throws Exception {
        runTests(6);
        assertEquals(true, true);
    }

    @Test
    public void testSeven() throws Exception {
        runTests(7);
        assertEquals(true, true);
    }

    @Test
    public void testEight() throws Exception {
        runTests(8);
        assertEquals(true, true);
    }

    @Test
    public void testNine() throws Exception {
        runTests(9);
        assertEquals(true, true);
    }

}