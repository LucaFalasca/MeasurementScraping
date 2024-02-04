package it.lucafalasca;

import it.lucafalasca.cold_start.ColdStart;
import it.lucafalasca.entities.ModFile;
import it.lucafalasca.entities.Release;
import it.lucafalasca.entities.RepoFile;
import it.lucafalasca.enumerations.Metric;
import it.lucafalasca.enumerations.Project;
import it.lucafalasca.populate.PopulateLocalFiles;
import it.lucafalasca.util.JsonReader;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException {
        Project project = Project.BOOKKEEPER;
        //PopulateLocalFiles.populateLocalJsonClasses(project);


        PopulateLocalFiles.populateCSVFileV2(project);
        //PopulateLocalFiles.tickets();
        //ColdStart.coldStart();
        //PopulateLocalFiles.populateCSVFile(project);
        //PopulateLocalFiles.statsOnReleases(project);
    }
}

