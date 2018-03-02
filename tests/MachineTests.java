import junit.framework.TestCase;
import model.Machine;
import model.constraints.Constraint;
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
        List<Constraint> constraints = testCase.getConstraints();
        FinalState state = machine.satisfyConstraints(constraints);
        assertStateMatch(state, constraints);
    }

    @Test
    public void testCase1() throws Exception {
        CoreTestCase testCase = new CoreTestCase(1);
        Machine machine = testCase.createMachine();
        List<Constraint> constraints = testCase.getConstraints();
        FinalState state = machine.satisfyConstraints(constraints);
        assertStateMatch(state, constraints);
    }

    private void assertStateMatch(State ans,  List<Constraint> constraints) {
        for (Constraint c : constraints) {
            if (c instanceof  InstallConstraint) {
                InstallConstraint c1 = (InstallConstraint) c;
                for (Package p : c1.optional.packages) {
                    boolean f = false;
                    for (Package p2 : ans.packages) {
                        if (p.name.equals(p2.name)) {
                            f = true;
                            break;
                        }
                    }
                    if (!f) Assert.fail("Missing constraint installation.");
                }
            } else if (c instanceof ForbiddenConstraint) {
                ForbiddenConstraint c1 = (ForbiddenConstraint) c;
                for (Package p : ans.packages) {
                    boolean f = false;
                    for (Package p2 : c1.packages) {
                        if (p.name.equals(p2.name)) {
                            f = true;
                            break;
                        }
                    }
                    if (f) Assert.fail("A forbidden constraint found in final solution.");
                }
            } else Assert.fail("Invalid constraint type...");
        }
        assertTrue("Constraints and state match. May not be valid.", true);
    }
}
