package util;

import org.json.JSONArray;
import repository.model.Conflict;
import repository.model.Dependants;
import repository.model.Operation;

import java.util.ArrayList;
import java.util.List;

public class JSONConverter {

    public static List<Conflict> parseConflicts(JSONArray arrConf) {
        List<Conflict> conf = new ArrayList<>();
        for (int j = 0; j < arrConf.length(); j++) {
            String s = arrConf.getString(j);
            if (s.contains(">=")) {
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

    public static List<Dependants> parseDependants(JSONArray arrDeps) {
        List<Dependants> deps = new ArrayList<>();
        for (int j = 0; j < arrDeps.length(); j++) {
            String s = arrDeps.getString(j);
            if (s.contains(">=")) {
                String[] p = s.split(">=");
                deps.add(new Dependants(p[0], p[1], Operation.GREATER_THAN_OR_EQUAL_TO));
            } else if (s.contains("<=")) {
                String[] p = s.split("<=");
                deps.add(new Dependants(p[0], p[1], Operation.LESS_THAN_OR_EQUAL_TO));
            } else if (s.contains("<")) {
                String[] p = s.split("<");
                deps.add(new Dependants(p[0], p[1], Operation.LESS_THAN));
            } else if (s.contains(">")) {
                String[] p = s.split(">");
                deps.add(new Dependants(p[0], p[1], Operation.GREATER_THAN));
            } else if (s.contains("=")) {
                String[] p = s.split("=");
                deps.add(new Dependants(p[0], p[1], Operation.EQUAL_TO));
            } else {
                deps.add(new Dependants(s, null, Operation.NONE));
            }
        }
        return deps;
    }
}