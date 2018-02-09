package util;

import repository.model.Operation;

public class VersionChecker {

    public static boolean versionEvaluate(String left, String right, Operation operation) {
        String[] leftS = left.split(".");
        String[] rightS = right.split(".");

        int itLength = Math.min(leftS.length, rightS.length);
        for (int i = 0; i < itLength; i++) {
            if (operation.evaluate(Integer.parseInt(leftS[i]), Integer.parseInt(rightS[i]))) return true;
        }
        return false;
    }
}
