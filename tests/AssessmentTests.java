import junit.framework.TestCase;
import model.Machine;
import model.constraints.ConstraintsPair;
import org.junit.Test;
import repository.PackageRepository;

import static util.Setup.*;

public class AssessmentTests extends TestCase {

    private final String basePath = System.getProperty("user.dir") + "\\tests\\resources\\assessment";

    public void runTest(int testNum) throws Exception {
        String fullPath = basePath + "\\seen-" + testNum + "\\";
        PackageRepository repo = new PackageRepository(readRepository(fullPath + "repository.json"));
        ConstraintsPair constraints = readConstraints(fullPath + "constraints.json", repo);
        Main.start(Machine.create(createState(fullPath + "initial.json", repo, constraints.forbidden), repo), constraints.install, constraints.forbidden);
    }

    @Test
    public void testZero() throws Exception {
        runTest(0);
        assertEquals(true, true);
    }

    @Test
    public void testOne() throws Exception {
        runTest(1);
        assertEquals(true, true);
    }

    @Test
    public void testTwo() throws Exception {
        runTest(2);
        assertEquals(true, true);
    }

    @Test
    public void testThree() throws Exception {
        runTest(3);
        assertEquals(true, true);
    }

    @Test
    public void testFour() throws Exception {
        runTest(4);
        assertEquals(true, true);
    }

    @Test
    public void testFive() throws Exception {
        runTest(5);
        assertEquals(true, true);
    }

    @Test
    public void testSix() throws Exception {
        runTest(6);
        assertEquals(true, true);
    }

    @Test
    public void testSeven() throws Exception {
        runTest(7);
        assertEquals(true, true);
    }

    @Test
    public void testEight() throws Exception {
        runTest(8);
        assertEquals(true, true);
    }

    @Test
    public void testNine() throws Exception {
        runTest(9);
        assertEquals(true, true);
    }

}