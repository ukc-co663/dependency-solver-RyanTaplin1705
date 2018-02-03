import machine.State;
import org.json.JSONArray;
import repository.DependencyRepository;
import repository.model.Dependency;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.HashMap;
import java.util.List;

import static machine.State.isValid;
import static util.FileReader.readFile;
import static util.Setup.getInitialState;
import static util.Setup.getRepository;

public class Main {

    public static State machine;

    public static void main(String[] args) {
        DependencyRepository repository = new DependencyRepository(getRepository(args[0]));
        List<Dependency> initialState = getInitialState(args[1], repository);

        JSONArray array = new JSONArray(readFile(args[2]));
        HashMap<String, String> constraints = new HashMap<>();
        for (int i = 0; i < array.length(); i++) {
            String[] dep = array.getString(i).split("");
            constraints.put(dep[0], dep[1]);
        }

         if (isValid(initialState))    /* TODO: is validation of initial state necessary? */
            machine = new State(repository, initialState);
         else
             throw new InvalidStateException("Initial state is not valid.");

         //TODO: Need Sys.In
         String input = "+A";
         if (input.contains("-")) {
             machine.uninstall("name");
         } else if (input.contains("+")) {
             machine.install("name");
         }
    }
}
