package it.lucafalasca.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ArffMaker {
    private static final String ARFF_BASE_PATH = "src/main/resources/arff_files/";

    public static void csvToArff(String path) throws IOException {
        System.out.println(path.lastIndexOf("/"));
        String substring = path.substring(path.lastIndexOf("/") + 1 , path.length() - 4);
        String filePath = ARFF_BASE_PATH + "ARFF_" + substring + ".arff";
        File file = new File(filePath);

        List<String[]> csv = CsvHandler.readCsv(path);
        String[] columnNames = csv.get(0);

        String[] header = new String[columnNames.length];
        header[0] = "@relation " + substring;
        for(int i = 1 ; i < columnNames.length - 2; i++) {
            header[i] = "@attribute " + columnNames[i + 1] + " numeric";
        }
        header[columnNames.length - 2] = "@attribute " + columnNames[columnNames.length - 1] + " {0, 1}";
        header[columnNames.length - 1] = "@data";

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
        for(String h : header) {
            bufferedWriter.write(h);
            bufferedWriter.newLine();
        }

        for(int i = 1 ; i < csv.size(); i++) {
            String[] row = csv.get(i);
            for(int j = 2 ; j < row.length; j++) {
                bufferedWriter.write(row[j] + ",");
            }
            bufferedWriter.write(row[row.length - 1]);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }
}
