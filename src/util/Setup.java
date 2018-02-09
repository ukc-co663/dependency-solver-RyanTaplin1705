package util;

import machine.State;
import model.Instruction;
import org.json.JSONArray;
import org.json.JSONObject;
import repository.DependencyRepository;
import repository.model.Conflict;
import repository.model.Dependants;
import repository.model.Dependency;
import sun.plugin.dom.exception.InvalidStateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static util.FileReader.readFile;

public class Setup {

    public static State getMachine(String repoPath, String iSPath) {
        DependencyRepository repository = new DependencyRepository(getRepository(repoPath));
        HashMap<String, Dependency> initialState = getInitialState(iSPath, repository);
        return new State(repository, initialState);
    }

    private static HashMap<String, LinkedList<Dependency>> getRepository(String filePath) {
        JSONArray repo = new JSONArray(readFile(filePath));

        HashMap<String, LinkedList<Dependency>> deps = new HashMap<>();
        for (int i = 0; i < repo.length(); i ++) {
            JSONObject object = repo.getJSONObject(i);
            List<Conflict> conf = JSONConverter.parseConflicts(object.getJSONArray("conflicts"));
            List<Dependants> dependants = JSONConverter.parseDependants(object.getJSONArray("depends"));

            String name = object.getString("name");
            if (deps.containsKey(name)) {
                LinkedList<Dependency> dependencies = deps.get(name);
                dependencies.add(new Dependency(name, object.getString("version"),
                        Integer.parseInt(object.getString("size")), dependants, conf));
                deps.put(name, dependencies);
            }
        }
        return deps;
    }

    private static HashMap<String, Dependency> getInitialState(String arg, DependencyRepository dr) {
        JSONArray json = new JSONArray(readFile(arg));
        HashMap<String, Dependency> initial = new HashMap<>(); // needs populating from .json files
        for (int i = 0; i < json.length(); i++) {
            String[] packSeg = json.getString(i).split("=");
            HashMap<String, LinkedList<Dependency>> dependencies = dr.getAllDependencies();
            if (dependencies.containsKey(packSeg[0])) {
                Dependency d = getDependencyOfVersion(packSeg[1], dependencies.get(packSeg[0]));
                initial.put(packSeg[0], new Dependency(packSeg[0], packSeg[1], 0, d.dependants, d.conflicts));
            } else throw new InvalidStateException("Error: No repo entry found for initial state package " + packSeg[0]);
        }
        return initial;
    }

    public static Dependency getDependencyOfVersion(String version, LinkedList<Dependency> dependencies) {
        for (int i = 0; i < dependencies.size(); i++) {
            if (dependencies.get(i).version.equals(version)) return dependencies.get(i);
        }
        throw new InvalidStateException(dependencies.get(0).name + " does not have version: " + version);
    }

    public static List<Instruction> getInstructions(String path) {
        JSONArray array = new JSONArray(readFile(path));
        List<Instruction> instructions = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Instruction instruction = Instruction.create(array.getString(i));
            instructions.add(instruction);
        }
        return instructions;
    }
}
