package util;

public class VersionChecker {
    public static boolean versionGreaterThan(String resolve, String comp) {
        String[] resolveS = resolve.split(".");
        String[] compS = comp.split(".");

        int itLength = Math.min(compS.length, resolveS.length);
        for (int i = 0; i < itLength; i++) {
            if (Integer.parseInt(resolveS[i]) > Integer.parseInt(compS[i])) return true;
        }
        return false;
    }
}
