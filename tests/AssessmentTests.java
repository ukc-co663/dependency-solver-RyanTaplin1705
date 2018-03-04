import junit.framework.TestCase;
import model.constraints.ConstraintsPair;
import model.constraints.ForbiddenConstraint;
import model.constraints.InstallConstraint;
import model.states.FinalState;
import org.junit.Assert;
import org.junit.Test;
import repository.model.Package;
import util.AssessmentTestCase;

import java.util.List;

public class AssessmentTests extends TestCase {

    @Test
    public void testZero() throws Exception {
        int BEST_COST = 9;
        AssessmentTestCase testCase = new AssessmentTestCase(0);
        ConstraintsPair constraints = testCase.getConstraints();
        FinalState state = testCase.createMachine().satisfyConstraints(constraints.install, constraints.forbidden);
        assertStateMatch(state, constraints.install, constraints.forbidden, BEST_COST);
    }

    @Test
    public void testOne() throws Exception {
        int BEST_COST = -1;
        AssessmentTestCase testCase = new AssessmentTestCase(1);
        ConstraintsPair constraints = testCase.getConstraints();
        FinalState state = testCase.createMachine().satisfyConstraints(constraints.install, constraints.forbidden);
        assertStateMatch(state, constraints.install, constraints.forbidden, BEST_COST);
    }

    @Test
    public void testTwo() throws Exception {
        int BEST_COST = 40;
        AssessmentTestCase testCase = new AssessmentTestCase(2);
        ConstraintsPair constraints = testCase.getConstraints();
        FinalState state = testCase.createMachine().satisfyConstraints(constraints.install, constraints.forbidden);
        assertStateMatch(state, constraints.install, constraints.forbidden, BEST_COST);
    }

    @Test
    public void testThree() throws Exception {
        int BEST_COST = 10003;
        AssessmentTestCase testCase = new AssessmentTestCase(3);
        ConstraintsPair constraints = testCase.getConstraints();
        FinalState state = testCase.createMachine().satisfyConstraints(constraints.install, constraints.forbidden);
        assertStateMatch(state, constraints.install, constraints.forbidden, BEST_COST);
    }

    @Test
    public void testFour() throws Exception {
        int BEST_COST = 1000043;
        AssessmentTestCase testCase = new AssessmentTestCase(4);
        ConstraintsPair constraints = testCase.getConstraints();
        FinalState state = testCase.createMachine().satisfyConstraints(constraints.install, constraints.forbidden);
        assertStateMatch(state, constraints.install, constraints.forbidden, BEST_COST);
    }

    @Test
    public void testFive() throws Exception {
        int BEST_COST = 100;
        AssessmentTestCase testCase = new AssessmentTestCase(5);
        ConstraintsPair constraints = testCase.getConstraints();
        FinalState state = testCase.createMachine().satisfyConstraints(constraints.install, constraints.forbidden);
        assertStateMatch(state, constraints.install, constraints.forbidden, BEST_COST);
    }

    @Test
    public void testSix() throws Exception {
        int BEST_COST = 73000234;
        AssessmentTestCase testCase = new AssessmentTestCase(6);
        ConstraintsPair constraints = testCase.getConstraints();
        FinalState state = testCase.createMachine().satisfyConstraints(constraints.install, constraints.forbidden);
        assertStateMatch(state, constraints.install, constraints.forbidden, BEST_COST);
    }

    @Test
    public void testSeven() throws Exception {
        int BEST_COST = 41168;
        AssessmentTestCase testCase = new AssessmentTestCase(7);
        ConstraintsPair constraints = testCase.getConstraints();
        FinalState state = testCase.createMachine().satisfyConstraints(constraints.install, constraints.forbidden);
        assertStateMatch(state, constraints.install, constraints.forbidden, BEST_COST);
    }

    @Test
    public void testEight() throws Exception {
        int BEST_COST = 2764;
        AssessmentTestCase testCase = new AssessmentTestCase(8);
        ConstraintsPair constraints = testCase.getConstraints();
        FinalState state = testCase.createMachine().satisfyConstraints(constraints.install, constraints.forbidden);
        assertStateMatch(state, constraints.install, constraints.forbidden, BEST_COST);
    }

    @Test
    public void testNine() throws Exception {
        int BEST_COST = 1385294336;
        AssessmentTestCase testCase = new AssessmentTestCase(9);
        ConstraintsPair constraints = testCase.getConstraints();
        FinalState state = testCase.createMachine().satisfyConstraints(constraints.install, constraints.forbidden);
        assertStateMatch(state, constraints.install, constraints.forbidden, BEST_COST);
    }

    private void assertStateMatch(FinalState ans, List<InstallConstraint> ics, List<ForbiddenConstraint> fcs, int BEST_COST) throws Exception {
        ans.printResults();
        if (ans.cost() > BEST_COST) Assert.fail("Cost has exceeded optimum.");
        System.out.println("COST: " + ans.cost());
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