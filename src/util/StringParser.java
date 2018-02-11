package util;

public class StringParser {

    public static String extractNameFromString(String string) {
        return string.substring(0, string.contains("=") ? string.indexOf("=") : 2);
    }

    public static String extractVersionFromString(String string) {
        return string.substring(string.indexOf("=") + 1, string.length());
    }

}
