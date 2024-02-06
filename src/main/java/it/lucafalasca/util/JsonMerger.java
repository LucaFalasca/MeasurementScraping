package it.lucafalasca.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JsonMerger {

    private JsonMerger() {}

    public static String mergeStringInJsonArrayString(List<String> strings) {
        StringBuilder bld = new StringBuilder();
        bld.append("[");
        for(String s : strings) {
            bld.append(s);
            bld.append(",");
        }
        String finalString = bld.toString();
        finalString = finalString.substring(0, finalString.length() - 1);
        finalString += "]";
        return finalString;
    }

    public static void writeStringOnFile(String fileName, String string) throws IOException {
        String path = "src/main/resources/json_files/" + fileName + ".json";
        String directoryPath = path.substring(0, path.lastIndexOf("/"));
        File directory = new File(directoryPath);
        if(!directory.exists() && !directory.mkdirs())
            throw new IOException("Cannot create directory " + directoryPath);
        try(FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.write(string);
        }
    }

    public static String addAttrInJsonObjectString(String jsonObjectString, String key, String value){
        jsonObjectString = jsonObjectString.substring(0, jsonObjectString.length() - 1);
        StringBuilder bld = new StringBuilder();
        bld.append(jsonObjectString);
        bld.append(",\"");
        bld.append(key);
        bld.append("\": \"");
        bld.append(value);
        bld.append("\"}");
        return bld.toString();
    }
}
