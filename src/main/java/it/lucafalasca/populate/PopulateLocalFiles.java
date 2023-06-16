package it.lucafalasca.populate;

import it.lucafalasca.Repository;
import it.lucafalasca.entities.*;
import it.lucafalasca.enumerations.Metric;
import it.lucafalasca.enumerations.Project;
import it.lucafalasca.measurement.MeasuringUnit;
import it.lucafalasca.measurement.MeasuringUnitConcrete;
import it.lucafalasca.util.CsvHandler;
import it.lucafalasca.util.Decode;
import it.lucafalasca.util.JsonMerger;
import it.lucafalasca.util.JsonReader;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PopulateLocalFiles {

    private PopulateLocalFiles(){

    }
    public static void populateLocalJsonClasses(Project project) throws IOException {
        Repository repository = new Repository(project);
        List<Release> releases;

        releases = repository.getReleases(repository.getFinalDate());
        for (Release r : releases) {
            System.out.println("Release: " + r.getVersionNumber());
            System.out.println("Release Date" + r.getReleaseDate());
            LocalDate date = LocalDate.parse(r.getReleaseDate());
            String treeUrl = repository.getTreeUrlFromDate(date);
            System.out.println(treeUrl + "?recursive=1");
            List<RepoFile> classes = repository.getClasses(treeUrl);
            List<String> jsonClasses = new ArrayList<>();
            for (RepoFile c : classes) {
                String jsonClass = JsonReader.readJsonFromUrl(c.getUrl(), true).toString();
                jsonClass = JsonMerger.addAttrInJsonObjectString(jsonClass, "path", c.getPath());
                jsonClasses.add(jsonClass);
            }
            String jsonRelease = JsonMerger.mergeStringInJsonArrayString(jsonClasses);
            JsonMerger.writeStringOnFile(repository.getProject() + "_classes/" + "CLASSES_" +  repository.getProject() + "_RELEASE_" + r.getVersionNumber(), jsonRelease);
        }
    }

    public static void populateCSVfile(Project project) throws IOException {
        Repository repository = new Repository(project);
        String[] columNames = Metric.getAllMetrics();
        List<String[]> data = new ArrayList<>();
        List<Release> releases;
        try {
            releases = repository.getReleases(repository.getFinalDate());
            LocalDate startRelease = null;
            LocalDate endRelease = null;

            for (Release r : releases) {
                if(r.getVersionNumber() == 2)
                    break;

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
                    mu = mu.addMetrics(Metric.LOC, Metric.NUM_IF);
                    measuringUnits.add(mu);
                }
                System.out.println("Classes (" + classes.size() + ") ");
                endRelease = LocalDate.parse(r.getReleaseDate());
                List<Commit> commits = repository.getCommits(startRelease, endRelease);
                startRelease = endRelease;
                for (Commit commit : commits) {
                    List<ModFile> modFiles = repository.getModFilesFromCommit(commit);
                    List<ClassContent> classContentList = repository.getClassesContent(r.getVersionNumber());
                    for (MeasuringUnit mu : measuringUnits) {
                        for (ModFile modFile : modFiles) {
                            if (modFile.getFilename().equals(mu.getRepoClass().getPath())) {
                                mu.calculateMetric(Metric.LOC, modFile);

//                                String t = mu.getValueFromMetric(Metric.LOC);
//                                int currentLoc = Integer.parseInt(t);
//                                currentLoc += modFile.getAdditions() - modFile.getDeletions();
//                                mu.setMetricValue(Metric.LOC, String.valueOf(currentLoc));

                            }
                        }
                        int c = 0;
                        for(ClassContent classContent : classContentList){
                            if(classContent.getPath().equals(mu.getRepoClass().getPath())) {
                                c++;
                                mu.calculateMetric(Metric.NUM_IF, classContent);
                            }
                        }
                        System.out.println(c);
                    }
                }

                for (MeasuringUnit mu : measuringUnits) {
                    Map<Metric, String> metrics = mu.getMetrics();
                    data.add(new String[]{
                            metrics.get(Metric.RELEASE),
                            metrics.get(Metric.CLASS),
                            metrics.get(Metric.LOC),
                            metrics.get(Metric.NUM_IF)});
                }


            }


        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            CsvHandler.writeCsv("DATASET_" + repository.getProject(), columNames, data);
        }
    }
}
