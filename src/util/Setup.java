package util;

import org.json.JSONArray;
import org.json.JSONObject;
import repository.DependencyRepository;
import repository.model.Conflicts;
import repository.model.Dependants;
import repository.model.Dependency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static util.FileReader.readFile;

public class Setup {
    public static HashMap<String, Dependency> getRepository(String filePath) {
        JSONArray repo = new JSONArray(readFile(filePath));
        HashMap<String, Dependency> deps = new HashMap<>();
        for (int i = 0; i < repo.length(); i ++) {
            JSONObject object = repo.getJSONObject(i);
            List<Conflicts> conf = JSONConverter.parseConflicts(object.getJSONArray("conflicts"));
            List<Dependants> dependants = JSONConverter.parseDependants(object.getJSONArray("depends"));
            String name = object.getString("name");
            deps.put(name, new Dependency(name,
                    object.getString("version"),
                    Integer.parseInt(object.getString("size")),
                    dependants,
                    conf));
        }
        return deps;
    }

    public static List<Dependency> getInitialState(String arg, DependencyRepository dr) {
        JSONArray initial = new JSONArray(readFile(arg));
        List<Dependency> initialState = new ArrayList<>(); // needs populating from .json files
        for (int i = 0; i < initial.length(); i++) {
            String[] dep = initial.getString(i).split("=");
            HashMap<String, Dependency> dependencies = dr.getAllDependencies();
            if (dependencies.containsKey(dep[0]))
                initialState.add(new Dependency(dep[0], dep[1], 0, dependencies.get(dep[0]).dependants, dependencies.get(dep[0]).conflicts));
            else
                System.out.println("Error: No repo entry found for initial state package " + dep[0]);
        }
        return initialState;
    }
}
