import model.Machine;
import model.constraints.ConstraintsPair;
import model.constraints.ForbiddenConstraint;
import model.constraints.InstallConstraint;
import model.states.State;
import repository.PackageRepository;

import java.util.List;

import static util.Setup.*;

public class Main {

    public static void main(String[] args) throws Exception {
        PackageRepository packageRepository = new PackageRepository(readRepository(args[0])); // repo.json
        ConstraintsPair constraints = readConstraints(args[2], packageRepository); // constraints.json
        State initialState = createState(args[1], packageRepository, constraints.forbidden); //initial.json

        start(Machine.create(initialState, packageRepository), constraints.install, constraints.forbidden);
    }

    public static void start(Machine m, List<InstallConstraint> ics, List<ForbiddenConstraint> fcs) throws Exception {
        m.satisfyConstraints(ics, fcs).printResults();
    }
}
