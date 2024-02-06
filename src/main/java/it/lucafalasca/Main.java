package it.lucafalasca;

import it.lucafalasca.enumerations.Project;
import it.lucafalasca.populate.PopulateLocalFiles;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Project project = Project.BOOKKEEPER;
        //PopulateLocalFiles.populateLocalJsonClasses(project);


        //PopulateLocalFiles.populateCSVFileV2(project);
        //PopulateLocalFiles.tickets();
        //ColdStart.coldStart();
        //PopulateLocalFiles.populateCSVFile(project);
        //PopulateLocalFiles.statsOnReleases(project);
        //ArffMaker.csvToArff("src/main/resources/csv_files/DATASET_BOOKKEEPER_RELEASE_6_TEST_2024_02_05@10_53.csv");

        try {
            PopulateLocalFiles.doExperiments(project);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

