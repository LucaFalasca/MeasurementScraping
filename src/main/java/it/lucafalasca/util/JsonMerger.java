package it.lucafalasca.util;

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
        try(FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.write(string);
        }
    }
}
