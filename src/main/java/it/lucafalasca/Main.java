package it.lucafalasca;

import it.lucafalasca.entities.Release;
import it.lucafalasca.entities.RepoFile;
import it.lucafalasca.enumerations.Metric;
import it.lucafalasca.enumerations.Project;
import it.lucafalasca.measurement.MeasuringUnit;
import it.lucafalasca.measurement.MeasuringUnitConcrete;
import it.lucafalasca.util.CsvHandler;
import it.lucafalasca.util.JsonMerger;
import it.lucafalasca.util.JsonReader;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {

        Repository repository = new Repository(Project.AVRO);
        String[] columNames = Metric.getAllMetrics();
        List<String[]> data = new ArrayList<>();
        List<Release> releases;



        try {
            releases = repository.getReleases(repository.getFinalDate());
            LocalDate startRelease = null;
            LocalDate endRelease = null;
            List<String> jsonReleases = new ArrayList<>();
            for (Release r : releases) {


                System.out.println("Release: " + r.getVersionNumber());
                System.out.println("Release Date" + r.getReleaseDate());
                LocalDate date = LocalDate.parse(r.getReleaseDate());
                String treeUrl = repository.getTreeUrlFromDate(date);
                System.out.println(treeUrl + "?recursive=1");
                List<RepoFile> classes = repository.getClasses(treeUrl);

                List<MeasuringUnit> measuringUnits = new ArrayList<>();
                List<String> jsonClasses = new ArrayList<>();
                for (RepoFile c : classes) {
                    MeasuringUnit mu = new MeasuringUnitConcrete(r, c);
                    mu = mu.addMetrics(Metric.LOC);
                    measuringUnits.add(mu);

                    jsonClasses.add(JsonReader.readJsonFromUrl(c.getUrl(), true).toString());
                }
                jsonReleases.add(JsonMerger.mergeStringInJsonArrayString(jsonClasses));
                /*
                System.out.println("Classes (" + classes.size() + ") ");
                endRelease = LocalDate.parse(r.getReleaseDate());
                List<Commit> commits = repository.getCommits(startRelease, endRelease);
                startRelease = endRelease;
                for (Commit commit : commits) {
                    List<ModFile> modFiles = repository.getModFilesFromCommit(commit);
                    for (ModFile modFile : modFiles) {
                        for (MeasuringUnit mu : measuringUnits) {
                            if (modFile.getFilename().equals(mu.getRepoClass().getPath())) {
                                int currentLoc = Integer.parseInt(mu.getValueFromMetric(Metric.LOC));
                                currentLoc += modFile.getAdditions() - modFile.getDeletions();
                                mu.setMetricValue(Metric.LOC, String.valueOf(currentLoc));
                            }
                        }
                    }
                }

                for (MeasuringUnit mu : measuringUnits) {
                    Map<Metric, String> metrics = mu.getMetrics();
                    data.add(new String[]{
                            metrics.get(Metric.RELEASE),
                            metrics.get(Metric.CLASS),
                            metrics.get(Metric.LOC)});
                }

                 */
            }
            String s = JsonMerger.mergeStringInJsonArrayString(jsonReleases);
            JsonMerger.writeStringOnFile("releases", s);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            CsvHandler.writeCsv("DATASET_" + repository.getProject(), columNames, data);
        }

    }
}