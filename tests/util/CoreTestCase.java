package util;

import model.Machine;
import model.constraints.ConstraintsPair;
import model.states.State;
import repository.PackageRepository;

import java.util.LinkedList;

import static util.Setup.*;

public class CoreTestCase {

    private final int num;
    private final PackageRepository repository;
    private final State initialState;
    private final ConstraintsPair constraints;
    private final State solution;

    public CoreTestCase(int num) throws Exception {
        this.num = num;

        String path = System.getProperty("user.dir") + "\\tests\\resources\\core\\testcase-" + num + "\\";
        this.repository = new PackageRepository(readRepository(path + "repository.json"));
        this.constraints = readConstraints(path + "constraints.json", repository);
        this.initialState = createState(path + "initial.json", repository, constraints.forbidden);
        this.solution = new State(new LinkedList<>()); //todo impl
    }

    public PackageRepository getRepository() {
        return this.repository;
    }

    public State getInitialState() {
        return this.initialState;
    }

    public ConstraintsPair getConstraints() {
        return this.constraints;
    }

    public int getTestCaseNum() {
        return this.num;
    }

    public Machine createMachine() {
        return Machine.create(this.initialState, this.repository);
    }

    public State getSolution() {
        return solution;
    }
}
