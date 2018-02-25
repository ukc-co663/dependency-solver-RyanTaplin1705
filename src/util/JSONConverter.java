package util;

import org.json.JSONArray;
import org.json.JSONObject;
import repository.model.Conflict;
import repository.model.Dependant;
import model.Operation;

import java.util.ArrayList;
import java.util.List;

public class JSONConverter {

    public static JSONArray getJSONArray(JSONObject object, String conflicts) {
        try {
            return object.getJSONArray(conflicts);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public static List<Conflict> parseConflicts(JSONArray arrConf) {
        List<Conflict> conf = new ArrayList<>();
        for (int j = 0; j < arrConf.length(); j++) {
            String s = arrConf.getString(j);
            if (s.contains(">=")) { //todo check this. not remove all of the operator in some cases
                String[] p = s.split(">=");
                conf.add(new Conflict(p[0], p[1], Operation.GREATER_THAN_OR_EQUAL_TO));
            } else if (s.contains("<=")) {
                String[] p = s.split("<=");
                conf.add(new Conflict(p[0], p[1], Operation.LESS_THAN_OR_EQUAL_TO));
            } else if (s.contains("<")) {
                String[] p = s.split("<");
                conf.add(new Conflict(p[0], p[1], Operation.LESS_THAN));
            } else if (s.contains(">")) {
                String[] p = s.split(">");
                conf.add(new Conflict(p[0], p[1], Operation.GREATER_THAN));
            } else if (s.contains("=")) {
                String[] p = s.split("=");
                conf.add(new Conflict(p[0], p[1], Operation.EQUAL_TO));
            } else {
                conf.add(new Conflict(s, null, Operation.NONE));
            }
        }
        return conf;
    }

    public static List<Dependant> parseDependants(JSONArray arrDeps) {
        List<Dependant> deps = new ArrayList<>();
        for (int i = 0; i < arrDeps.length(); i++) {
            JSONArray array = arrDeps.optJSONArray(i);
            if (array != null) {
                for (int j = 0; j < array.length(); j++) {
                    deps.add(createDep(array.getString(j)));
                }
            } else {
                deps.add(createDep(arrDeps.getString(i)));
            }
        }
        return deps;
    }

    private static Dependant createDep(String s) {
        if (s.contains(">=")) {
            String[] p = s.split(">=");
            return new Dependant(p[0], p[1], Operation.GREATER_THAN_OR_EQUAL_TO);
        } else if (s.contains("<=")) {
            String[] p = s.split("<=");
            return new Dependant(p[0], p[1], Operation.LESS_THAN_OR_EQUAL_TO);
        } else if (s.contains("<")) {
            String[] p = s.split("<");
            return new Dependant(p[0], p[1], Operation.LESS_THAN);
        } else if (s.contains(">")) {
            String[] p = s.split(">");
            return new Dependant(p[0], p[1], Operation.GREATER_THAN);
        } else if (s.contains("=")) {
            String[] p = s.split("=");
            return new Dependant(p[0], p[1], Operation.EQUAL_TO);
        } else {
            return new Dependant(s, null, Operation.NONE);
        }
    }
}
