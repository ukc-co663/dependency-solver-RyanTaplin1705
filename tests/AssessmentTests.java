import junit.framework.TestCase;
import model.Machine;
import model.constraints.ConstraintsPair;
import model.constraints.ForbiddenConstraint;
import model.constraints.InstallConstraint;
import model.states.FinalState;
import model.states.State;
import org.junit.Assert;
import org.junit.Test;
import repository.PackageRepository;
import repository.model.Package;
import util.AssessmentTestCase;
import util.CoreTestCase;

import java.util.List;

import static util.Setup.*;

public class AssessmentTests extends TestCase {

    @Test
    public void testZero() throws Exception {
        AssessmentTestCase testCase = new AssessmentTestCase(0);
        ConstraintsPair constraints = testCase.getConstraints();
        FinalState state = testCase.createMachine().satisfyConstraints(constraints.install, constraints.forbidden);
        assertStateMatch(state, constraints.install, constraints.forbidden);
    }

    @Test
    public void testOne() throws Exception {
        AssessmentTestCase testCase = new AssessmentTestCase(1);
        ConstraintsPair constraints = testCase.getConstraints();
        FinalState state = testCase.createMachine().satisfyConstraints(constraints.install, constraints.forbidden);
        assertStateMatch(state, constraints.install, constraints.forbidden);
    }

    @Test
    public void testTwo() throws Exception {
        AssessmentTestCase testCase = new AssessmentTestCase(2);
        ConstraintsPair constraints = testCase.getConstraints();
        FinalState state = testCase.createMachine().satisfyConstraints(constraints.install, constraints.forbidden);
        assertStateMatch(state, constraints.install, constraints.forbidden);
    }

    @Test
    public void testThree() throws Exception {
        AssessmentTestCase testCase = new AssessmentTestCase(3);
        ConstraintsPair constraints = testCase.getConstraints();
        FinalState state = testCase.createMachine().satisfyConstraints(constraints.install, constraints.forbidden);
        assertStateMatch(state, constraints.install, constraints.forbidden);
    }

    @Test
    public void testFour() throws Exception {
        AssessmentTestCase testCase = new AssessmentTestCase(4);
        ConstraintsPair constraints = testCase.getConstraints();
        FinalState state = testCase.createMachine().satisfyConstraints(constraints.install, constraints.forbidden);
        assertStateMatch(state, constraints.install, constraints.forbidden);
    }

    @Test
    public void testFive() throws Exception {
        AssessmentTestCase testCase = new AssessmentTestCase(5);
        ConstraintsPair constraints = testCase.getConstraints();
        FinalState state = testCase.createMachine().satisfyConstraints(constraints.install, constraints.forbidden);
        assertStateMatch(state, constraints.install, constraints.forbidden);
    }

    @Test
    public void testSix() throws Exception {
        AssessmentTestCase testCase = new AssessmentTestCase(6);
        ConstraintsPair constraints = testCase.getConstraints();
        FinalState state = testCase.createMachine().satisfyConstraints(constraints.install, constraints.forbidden);
        assertStateMatch(state, constraints.install, constraints.forbidden);
    }

    @Test
    public void testSeven() throws Exception {
        AssessmentTestCase testCase = new AssessmentTestCase(7);
        ConstraintsPair constraints = testCase.getConstraints();
        FinalState state = testCase.createMachine().satisfyConstraints(constraints.install, constraints.forbidden);
        assertStateMatch(state, constraints.install, constraints.forbidden);
    }

    @Test
    public void testEight() throws Exception {
        AssessmentTestCase testCase = new AssessmentTestCase(8);
        ConstraintsPair constraints = testCase.getConstraints();
        FinalState state = testCase.createMachine().satisfyConstraints(constraints.install, constraints.forbidden);
        assertStateMatch(state, constraints.install, constraints.forbidden);
    }

    @Test
    public void testNine() throws Exception {
        AssessmentTestCase testCase = new AssessmentTestCase(9);
        ConstraintsPair constraints = testCase.getConstraints();
        FinalState state = testCase.createMachine().satisfyConstraints(constraints.install, constraints.forbidden);
        assertStateMatch(state, constraints.install, constraints.forbidden);
    }

    private void assertStateMatch(FinalState ans, List<InstallConstraint> ics, List<ForbiddenConstraint> fcs) throws Exception {
        ans.printResults();
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