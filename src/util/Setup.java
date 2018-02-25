package util;

import model.Machine;
import model.State;
import model.exceptions.InvalidParsingException;
import model.instructions.Instruction;
import org.json.JSONArray;
import org.json.JSONObject;
import repository.DependencyRepository;
import repository.model.Conflict;
import repository.model.Dependant;
import repository.model.Dependency;
import model.Operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static model.Operation.NONE;
import static model.Operation.extractOperator;
import static util.FileReader.readFile;
import static util.JSONConverter.getJSONArray;
import static util.JSONConverter.parseConflicts;
import static util.JSONConverter.parseDependants;
import static util.StringParser.extractNameFromString;
import static util.StringParser.extractVersionFromString;

public class Setup {

    public static Machine getMachine(String repositoryPath, String initialStatePath, String constraintsPath) throws Exception {
        return new Machine(new State(repositoryPath, initialStatePath, constraintsPath));
    }

    public static HashMap<String, LinkedList<Dependency>> readRepository(String filePath) {
        JSONArray json = new JSONArray(readFile(filePath));

        HashMap<String, LinkedList<Dependency>> deps = new HashMap<>();
        for (int i = 0; i < json.length(); i ++) {
            JSONObject object = json.getJSONObject(i);

            int size = object.getInt("size");
            String name = object.getString("name");
            String version = object.getString("version");
            List<Conflict> conf = parseConflicts(getJSONArray(object, "confs"));
            List<Dependant> dependants = parseDependants(getJSONArray(object, "depends"));

            LinkedList<Dependency> tArr = deps.getOrDefault(name, new LinkedList());
            deps.put(name, genericArrayAdd(tArr, new Dependency(name, version, size, conf, dependants)));
        }
        return deps;
    }

    public static HashMap<String, LinkedList<Conflict>> readConstraints(String filePath) throws InvalidParsingException {
        JSONArray arr = new JSONArray(readFile(filePath));

        HashMap<String, LinkedList<Conflict>> deps = new HashMap<>();
        for (int i = 0; i < arr.length(); i ++) {
            String raw = arr.getString(i).substring(1, arr.getString(i).length());
            Operation op = extractOperator(raw);
            String name = raw.substring(0, raw.indexOf(op.getStringValue()));
            String version = op.equals(NONE) ? null :  raw.substring(raw.indexOf(op.getStringValue()), raw.length());

            LinkedList<Conflict> tArr = deps.getOrDefault(name, new LinkedList());
            deps.put(name, genericArrayAdd(tArr, new Conflict(name, version, op)));
        }
        return deps;
    }

    public static HashMap<String, Dependency> readInitialState(String filePath, DependencyRepository dr) throws Exception {
        JSONArray json = new JSONArray(readFile(filePath));
        HashMap<String, Dependency> initial = new HashMap<>(); // needs populating from .json files
        for (int i = 0; i < json.length(); i++) {
            String instr = json.getString(i);
            String name = extractNameFromString(instr);

            HashMap<String, LinkedList<Dependency>> dependencies = dr.getAllDependencies();
            if (dependencies.containsKey(name)) {
                if (!instr.contains("=")) {
                    for (Dependency d : dependencies.get(name)) {
                        initial.put(d.getKey(), d);
                    }
                } else {
                    Dependency d = getDependencyOfVersion(extractVersionFromString(instr), dependencies.get(name));
                    initial.put(d.getKey(), d);
                }
            } else throw new Exception("Can't find dependency " + name + " in the machine repository.");
        }
        return initial;
    }

    public static Dependency getDependencyOfVersion(String version, LinkedList<Dependency> dependencies) throws Exception {
        for (int i = 0; i < dependencies.size(); i++) {
            if (dependencies.get(i).version.equals(version)) return dependencies.get(i);
        }
        throw new Exception(dependencies.get(0).name + " does not have version: " + version);
    }

    public static List<Instruction> getInstructions(String constraintsPath, State machine) throws Exception {
        JSONArray json = new JSONArray(readFile(constraintsPath));
        List<Instruction> instructions = new ArrayList<>();
        for (int i = 0; i < json.length(); i++) {
            instructions.addAll(Instruction.create(json.getString(i), machine));
        }
        return instructions;
    }



    private static <T> LinkedList<T> genericArrayAdd(LinkedList<T> list, T result) {
        list.add(result);
        return list;
    }
}
