package it.lucafalasca.populate;

import it.lucafalasca.Repository;
import it.lucafalasca.entities.*;
import it.lucafalasca.enumerations.Metric;
import it.lucafalasca.enumerations.Project;
import it.lucafalasca.measurement.MeasuringUnit;
import it.lucafalasca.measurement.MeasuringUnitConcrete;
import it.lucafalasca.util.CsvHandler;
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
            LocalDate startRelease = LocalDate.of(1990, 10, 10);
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
                    mu = mu.addMetrics(Metric.CHURN,
                            Metric.LOC,
                            Metric.IF,
                            Metric.IMPORT,
                            Metric.CHANGES,
                            Metric.SEMICOLON,
                            Metric.COMMIT,
                            Metric.COMMENT,
                            Metric.PUBLIC,
                            Metric.PRIVATE,
                            Metric.PROTECTED,
                            Metric.BUGGYNESS);
                    measuringUnits.add(mu);
                }
                System.out.println("Classes (" + classes.size() + ") ");
                endRelease = LocalDate.parse(r.getReleaseDate());
                List<Commit> commits = repository.getCommits(startRelease, endRelease);
                List<Ticket> tickets = repository.getBugTickets(startRelease, endRelease);
                startRelease = endRelease;
                for (Commit commit : commits) {
                    List<ModFile> modFiles = repository.getModFilesFromCommit(commit);
                    List<ClassContent> classContentList = repository.getClassesContent(r.getVersionNumber());
                    for (MeasuringUnit mu : measuringUnits) {
                        for (ModFile modFile : modFiles) {
                            if (modFile.getFilename().equals(mu.getRepoClass().getPath())) {
                                mu.calculateMetric(Metric.CHURN, modFile);
                                mu.calculateMetric(Metric.CHANGES, modFile);
                                mu.calculateMetric(Metric.COMMIT, 1);
                            }
                        }
                        for(ClassContent classContent : classContentList){
                            if(classContent.getPath().equals(mu.getRepoClass().getPath())) {
                                mu.calculateMetric(Metric.IF, classContent);
                                mu.calculateMetric(Metric.IMPORT, classContent);
                                mu.calculateMetric(Metric.SEMICOLON, classContent);
                                mu.calculateMetric(Metric.LOC, classContent);
                                mu.calculateMetric(Metric.COMMENT, classContent);
                                mu.calculateMetric(Metric.PUBLIC, classContent);
                                mu.calculateMetric(Metric.PRIVATE, classContent);
                                mu.calculateMetric(Metric.PROTECTED, classContent);
                            }
                        }
                    }
                }

                for (MeasuringUnit mu : measuringUnits) {
                    Map<Metric, String> metrics = mu.getMetrics();
                    String[] singleData = new String[Metric.values().length];
                    for(Metric metric: Metric.values()){
                        singleData[metric.ordinal()] = metrics.get(metric);
                    }
                    data.add(singleData);
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

    public static void tickets() throws IOException {
        Repository repository = new Repository(Project.BOOKKEEPER);
        System.out.println(repository.getBugTickets(null, null));
    }
}
