package util;

import org.json.JSONArray;

import java.io.IOException;

public class FileWriter {

    private java.io.FileWriter fileWriter;

    public FileWriter(String outputPath) throws IOException {
        fileWriter = new java.io.FileWriter(outputPath);
    }

    public static FileWriter create(String outputPath) throws IOException {
        return new FileWriter(outputPath);
    }

    public void writeJSON(JSONArray arr) throws IOException {
        fileWriter.write(arr.toString());
        fileWriter.flush();
    }
}
