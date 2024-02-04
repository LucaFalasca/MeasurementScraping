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

           /* Release r = releases.get(0);
            Set<ModFile> mf = PopulateLocalFiles.getBuggedFilesInRelease(project, r, startRelease);
            System.out.println("Classi Buggate nella release " + r.getName() + ": " + mf.size());*/

            /*System.out.println(mf);
            for(ModFile m : mf){
                Set<ModFile> mf2 = new LinkedHashSet<>();
                mf2.addAll(mf);
                mf2.remove(m);
                for(ModFile m2 : mf2) {
                    if (m2.equals(m)) {
                        System.out.println(m.getFilename() + " " + m2.getFilename() + " " +  mf2.contains(m));
                    }
                }
            }*/
            for(int i = 0; i < releases.size() - 1; i++) {
                Release r = releases.get(i);
                Release r2 = releases.get(i + 1);
                startRelease = LocalDate.parse(r.getReleaseDate());
                List<RepoFile> files = PopulateLocalFiles.getFilesInRelease(project, r);
                Set<ModFile> files2 = PopulateLocalFiles.getBuggedFilesInRelease(project, r2, startRelease);
                System.out.println("---------RELEASE " + r.getVersionNumber() + "->" + r2.getVersionNumber() + "---------");
                System.out.println("Classi Fixate nella release " + r2.getVersionNumber() + ": " + files2.size());
                startRelease = LocalDate.parse(r2.getReleaseDate());

                if (files2.size() == 0) {
                    System.out.println("Nessuna classe Fixata nella release " + r2.getVersionNumber());
                }
                else{
                    int classesSurvivors = 0;
                    int renamedClasses = 0;
                    for (RepoFile f : files) {
                        for (ModFile m3 : files2) {
                            if (f.getPath().equals(m3.getFilename())) {
                                classesSurvivors++;
                            } else if (f.getPath().substring(f.getPath().lastIndexOf("/") + 1).equals(m3.getFilename().substring(m3.getFilename().lastIndexOf("/") + 1))) {
                                renamedClasses++;
                            }
                        }

                    }
                    System.out.println("Classi sopravvissute: " + classesSurvivors + "(" + (classesSurvivors / files2.size()) + "%)");
                    System.out.println("Classi rinominate: " + renamedClasses + "(" + (renamedClasses / files2.size()) + "%)");
                    System.out.println("Classi fixate nella release: " + (files2.size() - classesSurvivors - renamedClasses) + "(" + (files2.size() - classesSurvivors - renamedClasses) / files2.size() + "%)");
                }
            }




        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

