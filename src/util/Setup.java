package util;

import machine.State;
import model.Instruction;
import org.json.JSONArray;
import org.json.JSONObject;
import repository.DependencyRepository;
import repository.model.Conflict;
import repository.model.Dependants;
import repository.model.Dependency;
import repository.model.Operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static repository.model.Operation.NONE;
import static repository.model.Operation.extract;
import static util.FileReader.readFile;

public class Setup {

    public static State getMachine(String basePath) throws Exception {
        return new State(basePath);
    }

    public static HashMap<String, LinkedList<Dependency>> getRepository(String filePath) {
        JSONArray repo = new JSONArray(readFile(filePath));

        HashMap<String, LinkedList<Dependency>> deps = new HashMap<>();
        for (int i = 0; i < repo.length(); i ++) {
            JSONObject object = repo.getJSONObject(i);
            List<Conflict> conf = JSONConverter.parseConflicts(getJsonArray(object, "confs"));
            List<Dependants> dependants = JSONConverter.parseDependants(getJsonArray(object, "depends"));
            String name = object.getString("name");
            String version = object.getString("version");
            int size = object.getInt("size");
            if (deps.containsKey(name)) {
                LinkedList<Dependency> dependencies = deps.get(name);
                dependencies.add(new Dependency(name, version, size, conf, dependants));
                deps.put(name, dependencies);
            } else {
                LinkedList<Dependency> dependencies = new LinkedList<>();
                dependencies.add(new Dependency(name, version, size, conf, dependants));
                deps.put(name, dependencies);
            }
        }
        return deps;
    }

    public static HashMap<String, LinkedList<Conflict>> getConstraints(String filePath) {
        JSONArray arr = new JSONArray(readFile(filePath));

        HashMap<String, LinkedList<Conflict>> deps = new HashMap<>();
        for (int i = 0; i < arr.length(); i ++) {
            String s = arr.getString(i).substring(1, arr.getString(i).length());
            Operation op = extract(s);
            String name = s.substring(0, s.indexOf(op.getStringValue()));
            String version = op.equals(NONE) ? null :  s.substring(s.indexOf(op.getStringValue()), s.length());
            if (deps.containsKey(name)) {
                LinkedList<Conflict> conf = deps.get(name);
                conf.add(new Conflict(name, version, op));
                deps.put(name, conf);
            } else {
                LinkedList<Conflict> conf = new LinkedList<>();
                conf.add(new Conflict(name, version, op));
                deps.put(name, conf);
            }
        }
        return deps;
    }

    private static JSONArray getJsonArray(JSONObject object, String conflicts) {
        try {
            return object.getJSONArray(conflicts);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public static HashMap<String, Dependency> getInitialState(String filePath, DependencyRepository dr) {
        JSONArray json = new JSONArray(readFile(filePath));
        HashMap<String, Dependency> initial = new HashMap<>(); // needs populating from .json files
        for (int i = 0; i < json.length(); i++) {
            String[] packSeg = json.getString(i).split("=");
            HashMap<String, LinkedList<Dependency>> dependencies = dr.getAllDependencies();
            if (dependencies.containsKey(packSeg[0])) {
                Dependency d = getDependencyOfVersion(packSeg[1], dependencies.get(packSeg[0]));
                initial.put(packSeg[0], new Dependency(packSeg[0], packSeg[1], 0, d.confs, d.deps));
            } else System.out.println("Error: No repo entry found for initial state package " + packSeg[0]);
        }
        return initial;
    }

    public static Dependency getDependencyOfVersion(String version, LinkedList<Dependency> dependencies) {
        for (int i = 0; i < dependencies.size(); i++) {
            if (dependencies.get(i).version.equals(version)) return dependencies.get(i);
        }
        System.out.println(dependencies.get(0).name + " does not have version: " + version);
        return null;
    }

    public static List<Instruction> getInstructions(String basePath, State machine) throws Exception {
        JSONArray array = new JSONArray(readFile(basePath + "constraints.json"));
        List<Instruction> instructions = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            instructions.addAll(Instruction.create(array.getString(i), machine));
        }
        return instructions;
    }
}
