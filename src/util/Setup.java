package util;

import model.constraints.*;
import model.instructions.Instruction;
import model.instructions.RemoveInstruction;
import model.states.State;
import org.json.JSONArray;
import org.json.JSONObject;
import repository.OptionalPackages;
import repository.PackageRepository;
import repository.model.Package;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static util.FileReader.readFile;
import static util.JSONConverter.parseConflicts;
import static util.JSONConverter.parseDependants;
import static util.StringParser.extractNameFromString;
import static util.StringParser.extractVersionFromString;

public class Setup {

    // this needs to be done better....
    public static HashMap<String, LinkedList<Package>> readRepository(String path) throws Exception {
        JSONArray json = new JSONArray(readFile(path));

        //create base without dependants / conflicts
        HashMap<String, LinkedList<Package>> packages = new HashMap<>();
        for (int i = 0; i < json.length(); i ++) {
            JSONObject object = json.getJSONObject(i);

            int size = object.getInt("size");
            String name = object.getString("name");
            String version = object.getString("version");

            LinkedList<Package> tArr = packages.getOrDefault(name, new LinkedList());
            packages.put(name, genericArrayAdd(tArr, new Package(name, version, size, Arrays.asList(), Arrays.asList())));
        }

        //process json array to apply deps/confs
        for(int i = 0; i < json.length(); i++) {
            JSONObject object = json.getJSONObject(i);
            String name = object.getString("name");
            String version = object.getString("version");

            for(Package p : packages.get(name)) {
                if (p.version.equals(version)) {
                    p.dependants = parseDependants(object.optJSONArray("depends"), new PackageRepository(packages));
                    p.conflicts = parseConflicts(object.optJSONArray("conflicts"), new PackageRepository(packages));
                }
            }
        }
        return packages;
    }

    public static State createState(String filepath, PackageRepository repository, List<ForbiddenConstraint> forbiddens) throws Exception {
        return new State(readInitial(filepath, repository, forbiddens), uninstalled(forbiddens), new LinkedList<>());
    }

    private static LinkedList<Instruction> uninstalled(List<ForbiddenConstraint> forbiddens) {
        LinkedList<Instruction> res = new LinkedList<>();
        for (ForbiddenConstraint fc : forbiddens) {
            for (Package p : fc.packages) {
                res.add(new RemoveInstruction(p.name, p.version));
            }
        }
        return res;
    }

    private static LinkedList<Package> readInitial(String filePath, PackageRepository repository, List<ForbiddenConstraint> forbiddens) throws Exception {
        JSONArray json = new JSONArray(readFile(filePath));
        LinkedList<Package> initialState = new LinkedList<>(); // needs populating from .json files
        for (int i = 0; i < json.length(); i++) {
            String input = json.getString(i);
            Package aPackage = repository.getDependency(extractNameFromString(input))
                    .ofVersion(extractVersionFromString(input));

            if (!isForbidden(forbiddens, aPackage)) initialState.add(aPackage);
        }


        return initialState;
    }

    private static boolean isForbidden(List<ForbiddenConstraint> forbiddens, Package aPackage) {
        for (ForbiddenConstraint fc : forbiddens) {
            for (Package fp : fc.packages) {
                if (aPackage.name.equals(fp.name)) return true;
            }
        }
        return false;
    }

    public static ConstraintsPair readConstraints(String filePath, PackageRepository repository) throws Exception {
        JSONArray json = new JSONArray(readFile(filePath));
        LinkedList<Constraint> constraints = new LinkedList<>();
        for (int i = 0; i < json.length(); i++) {
            String input = json.getString(i);
            ParsedConstraint cp = Constraint.parseJSON(input);

            LinkedList<Package> p = repository.getDependency(cp.name).ofVersions(cp.op, cp.version);
            if(input.charAt(0) == '+') {
                constraints.add(new InstallConstraint(new OptionalPackages(p)));
            } else if (input.charAt(0) == '-') {
                constraints.add(new ForbiddenConstraint(p));
            } else throw new Exception("Constraint format is not recognised.");
        }
        return new ConstraintsPair(constraints);
    }

    private static <T> LinkedList<T> genericArrayAdd(LinkedList<T> list, T result) {
        list.add(result);
        return list;
    }
}
