package it.lucafalasca;

import it.lucafalasca.cold_start.ColdStart;
import it.lucafalasca.entities.ModFile;
import it.lucafalasca.entities.Release;
import it.lucafalasca.enumerations.Metric;
import it.lucafalasca.enumerations.Project;
import it.lucafalasca.populate.PopulateLocalFiles;
import it.lucafalasca.util.JsonReader;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        Project project = Project.BOOKKEEPER;
        //PopulateLocalFiles.populateLocalJsonClasses(project);


        //PopulateLocalFiles.populateCSVFileV2(project);
        //PopulateLocalFiles.tickets();
        //ColdStart.coldStart();
        //PopulateLocalFiles.populateCSVFile(project);
        Repository repository = new Repository(project);
        String[] columNames = Metric.getAllMetrics();
        List<String[]> data = new ArrayList<>();
        List<Release> releases;
        try {
            releases = repository.getReleases(repository.getFinalDate());
            LocalDate startRelease = LocalDate.of(1990, 10, 10);
            LocalDate endRelease = null;

            for (Release r : releases) {
                List<ModFile> mf = PopulateLocalFiles.getBuggedFilesInRelease(project, r, startRelease);
                System.out.println("Classi Buggate nella release " + r.getName() + ": " + mf.size());
                startRelease = LocalDate.parse(r.getReleaseDate());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

