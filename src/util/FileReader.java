package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;

public class FileReader {
    public static String readFile(String filePath) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new java.io.FileReader(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder result = new StringBuilder();
        reader.lines().forEach(line -> result.append(line));
        return result.toString();
    }
}
