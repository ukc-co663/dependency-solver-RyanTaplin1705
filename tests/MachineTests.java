import junit.framework.TestCase;
import model.Machine;
import model.constraints.ConstraintsPair;
import model.constraints.ForbiddenConstraint;
import model.constraints.InstallConstraint;
import model.states.FinalState;
import model.states.State;
import org.junit.Assert;
import org.junit.Test;
import repository.model.Package;
import util.CoreTestCase;

import java.util.List;

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
        ConstraintsPair constraints = testCase.getConstraints();
        FinalState state = machine.satisfyConstraints(constraints.install, constraints.forbidden);
        assertStateMatch(state, constraints.install, constraints.forbidden);
    }

    @Test
    public void testCase1() throws Exception {
        CoreTestCase testCase = new CoreTestCase(1);
        Machine machine = testCase.createMachine();
        ConstraintsPair constraints = testCase.getConstraints();
        FinalState state = machine.satisfyConstraints(constraints.install, constraints.forbidden);
        assertStateMatch(state, constraints.install, constraints.forbidden);
    }

    private void assertStateMatch(State ans,  List<InstallConstraint> ics, List<ForbiddenConstraint> fcs) {
        for(InstallConstraint ic : ics) {
            for (Package p : ic.optional.packages) {
                boolean f = false;
                for (Package p2 : ans.packages) {
                    if (p.name.equals(p2.name)) {
                        f = true;
                        break;
                    }
                }
                if (!f) Assert.fail("Missing constraint installation.");
            }
        }

        for (ForbiddenConstraint fc : fcs) {
            for (Package p : ans.packages) {
                boolean f = false;
                for (Package p2 : fc.packages) {
                    if (p.name.equals(p2.name)) {
                        f = true;
                        break;
                    }
                }
                if (f) Assert.fail("A forbidden constraint found in final solution.");
            }
        }
        assertTrue("Constraints and state match. May not be valid.", true);
    }
}
