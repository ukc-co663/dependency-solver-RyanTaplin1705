import junit.framework.TestCase;
import util.CoreTestCase;
import model.Machine;
import model.states.FinalState;
import model.states.State;
import org.junit.Assert;
import org.junit.Test;
import repository.model.Package;

public class MachineTests extends TestCase {

    //machine is created with empty initial state and valid package repository
    //machine is created with valid populated initial state and valid package repository
    //machine throws exception with invalid initial state and any package repository
    //todo -> implement logic of 'invalid initial state', or leave it and assume initial.json is always valid.
    //machine throws exception with any state and empty or invalid package repository
    //todo -> implement logic of 'invalid repository', or leave it and assume repository.json is always valid.

    @Test
    public void testCase0() throws Exception {
        CoreTestCase testCase = new CoreTestCase(0);
        Machine machine = testCase.createMachine();
        FinalState state = machine.satisfyConstraints(testCase.getConstraints());
        assertStateMatch(state, testCase.getSolution());
    }

    private void assertStateMatch(State ans,  State sol) {
        if (ans.packages.size() != sol.packages.size()) Assert.fail("Solution stats differ in size.");
        for(Package ap : ans.packages) {
            boolean f = false;
            for (Package sp : sol.packages) {
                if (ap.name.equals(sp.name)) {
                    f = true;
                    break;
                }
            }
            if (!f) Assert.fail("Package " + ap.name + " was not found recognised in solution.");
        }
    }
}
