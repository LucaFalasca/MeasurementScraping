package it.lucafalasca.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    public static String writeCsv(String fileName, String[] columnNames, List<String []> data, LocalDateTime date) throws IOException {
        String filePath = BASE_PATH + fileName + "_" + date.format(formatter) + ".csv";
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
        return filePath;
    }

    public static List<String[]> readCsv(String path) {
        List<String[]> ret = new ArrayList<>();
        try {
            // Create an object of file reader
            // class with CSV file as a parameter.
            FileReader filereader = new FileReader(path);

            // create csvReader object and skip first Line
            CSVReader csvReader = new CSVReaderBuilder(filereader).build();
            ret = csvReader.readAll();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}
