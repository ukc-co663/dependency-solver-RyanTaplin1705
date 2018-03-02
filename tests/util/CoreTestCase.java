package util;

import model.Machine;
import model.constraints.Constraint;
import model.states.State;
import repository.PackageRepository;

import java.util.LinkedList;
import java.util.List;

import static util.Setup.*;

public class CoreTestCase {

    private final int num;
    private final PackageRepository repository;
    private final State initialState;
    private final List<Constraint> constraints;
    private final State solution;

    public CoreTestCase(int num) throws Exception {
        this.num = num;

        String path = System.getProperty("user.dir") + "\\tests\\resources\\core\\testcase-" + num + "\\";
        this.repository = new PackageRepository(readRepository(path + "repository.json"));
        this.initialState = new State(readInitial(path + "initial.json", repository), new LinkedList<>(), new LinkedList<>());
        this.constraints = readConstraints(path + "constraints.json", repository);
        this.solution = new State(new LinkedList<>()); //todo impl
    }

    public PackageRepository getRepository() {
        return this.repository;
    }

    public State getInitialState() {
        return this.initialState;
    }

    public List<Constraint> getConstraints() {
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
