package util;

import model.Operation;
import model.constraints.ParsedConstraint;
import org.json.JSONArray;
import org.json.JSONObject;
import repository.ConflictPackages;
import repository.OptionalPackages;
import repository.PackageRepository;
import repository.model.Package;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class JSONConverter {

    public static JSONArray getJSONArray(JSONObject object, String conflicts) {
        try {
            return object.getJSONArray(conflicts);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public static List<ConflictPackages> parseConflicts(JSONArray json, PackageRepository packageRepository) throws Exception {
        LinkedList<ConflictPackages> result = new LinkedList<>();
        if (json == null) return result;
        for (int j = 0; j < json.length(); j++) {
            ParsedConstraint pc = convertJSON(json.getString(j));
            result.add(new ConflictPackages(packageRepository.getDependency(pc.name).ofVersions(pc.op, pc.version)));
        }
        return result;
    }

    public static List<OptionalPackages> parseDependants(JSONArray json, PackageRepository packageRepository) throws Exception {
        List<OptionalPackages> result = new ArrayList<>();
        if (json == null) return result;
        for (int i = 0; i < json.length(); i++) {
            JSONArray choices = json.optJSONArray(i);
            if (choices != null) {
                LinkedList<Package> packages = new LinkedList<>();
                for (int j = 0; j < choices.length(); j++) { // catering for multi-option dependant
                    ParsedConstraint pc = convertJSON(choices.getString(j));
                    packages.addAll(packageRepository.getDependency(pc.name).ofVersions(pc.op, pc.version));
                }
                result.add(new OptionalPackages(packages));
            } else {
                ParsedConstraint pc = convertJSON(json.getString(i)); // cater for singular option dependant
                result.add(new OptionalPackages(packageRepository.getDependency(pc.name).ofVersions(pc.op, pc.version)));
            }
        }
        return result;
    }

    private static ParsedConstraint convertJSON(String s) throws Exception {
        if (s.contains(">=")) {
            String[] p = s.split(">=");
            return new ParsedConstraint(p[0], p[1], Operation.GREATER_THAN_OR_EQUAL_TO);
        } else if (s.contains("<=")) {
            String[] p = s.split("<=");
            return new ParsedConstraint(p[0], p[1], Operation.LESS_THAN_OR_EQUAL_TO);
        } else if (s.contains("<")) {
            String[] p = s.split("<");
            return new ParsedConstraint(p[0], p[1], Operation.LESS_THAN);
        } else if (s.contains(">")) {
            String[] p = s.split(">");
            return new ParsedConstraint(p[0], p[1], Operation.GREATER_THAN);
        } else if (s.contains("=")) {
            String[] p = s.split("=");
            return new ParsedConstraint(p[0], p[1], Operation.EQUAL_TO);
        } else {
            return new ParsedConstraint(s, null, Operation.NONE);
        }
    }
}
