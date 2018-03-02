package util;

import model.Operation;

public class VersionChecker {

    public static boolean versionEvaluate(String left, String right, Operation operation) {
        if (left == null || right == null) return false; //TODO not sure if this is right...
        String[] leftS = getVersionAsList(left);
        String[] rightS = getVersionAsList(right);

        int itLength = Math.min(leftS.length, rightS.length);
        for (int i = 0; i < itLength; i++) {
            if (operation.equals(Operation.LESS_THAN) || operation.equals(Operation.GREATER_THAN)) {
                if (i == (itLength - 1)) {
                    return operation.evaluate(Integer.parseInt(rightS[i]), Integer.parseInt(leftS[i]));
                } else if (!Operation.EQUAL_TO.evaluate(Integer.parseInt(rightS[i]), Integer.parseInt(leftS[i]))) return false;
            } else if (!operation.evaluate(Integer.parseInt(rightS[i]), Integer.parseInt(leftS[i]))) return false;
        }
        return true;
    }

    //less than -->> 3.0 and 3.1
    // 3 less than 3 == false so if less or greater than check if equal

    private static String[] getVersionAsList(String l) {
        if (!l.contains(".")) {
          String[] s = new String[1];
            s[0] = l;
            return s;
        } else return l.split("\\.");
    }

}
