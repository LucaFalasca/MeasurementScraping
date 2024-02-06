package it.lucafalasca;

import it.lucafalasca.cold_start.ColdStart;
import it.lucafalasca.enumerations.Project;
import it.lucafalasca.populate.PopulateLocalFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws IOException {
        Project project = Project.BOOKKEEPER;
        PopulateLocalFiles.populateLocalJsonClasses(project);
        PopulateLocalFiles.populateDatasets(project);
        PopulateLocalFiles.statsOnReleases(project);
        ColdStart.coldStart();
        try {
            PopulateLocalFiles.doExperiments(project);
        } catch (Exception e) {
            logger.error("Error in experiments");
        }
    }
}

