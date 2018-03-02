import model.Machine;
import model.states.State;
import model.constraints.Constraint;
import repository.PackageRepository;

import java.util.LinkedList;
import java.util.List;

import static util.Setup.readConstraints;
import static util.Setup.readInitial;
import static util.Setup.readRepository;

public class Main {

    public static void main(String[] args) throws Exception {
        PackageRepository packageRepository = new PackageRepository(readRepository(args[0])); // repo.json
        State initialState = new State(readInitial(args[1], packageRepository)); //initial.json
        LinkedList<Constraint> constraints = readConstraints(args[2], packageRepository); // constraints.json

        start(Machine.create(initialState, packageRepository), constraints);
    }

    public static void start(Machine m, List<Constraint> cs) throws Exception {
        m.satisfyConstraints(cs).printResults();
    }
}
