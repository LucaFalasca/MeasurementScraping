package it.lucafalasca.util;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class CsvHandler {
    private CsvHandler() {}
    private static final String BASE_PATH = "src/main/resources/csv_files/";
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd@HH_mm");
    public static void writeCsv(String fileName, String[] columnNames, List<String []> data) throws IOException {

        String filePath = BASE_PATH + fileName + "_" + LocalDateTime.now().format(formatter) + ".csv";
        File file = new File(filePath);
        FileWriter fileWriter = new FileWriter(file, false);
        CSVWriter csvWriter = new CSVWriter(fileWriter);

        csvWriter.writeNext(columnNames);
        for (String[] line : data) {
            String[] formattedLine = Arrays.copyOf(line, columnNames.length);
            csvWriter.writeNext(formattedLine);
        }

        csvWriter.close();
        fileWriter.close();
    }
}
