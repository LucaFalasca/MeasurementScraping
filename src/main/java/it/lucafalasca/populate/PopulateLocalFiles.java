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
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    public static void populateCSVFile(Project project) throws IOException {
        Repository repository = new Repository(project);
        String[] columNames = Metric.getAllMetrics();
        List<String[]> data = new ArrayList<>();
        List<Release> releases;
        try {
            releases = repository.getReleases(repository.getFinalDate());
            LocalDate startRelease = LocalDate.of(1990, 10, 10);
            LocalDate endRelease = null;

            for (Release r : releases) {
               /* if(r.getVersionNumber() == 2)
                    break;*/

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

    public static void populateCSVFileV2(Project project) throws IOException {
        Repository repository = new Repository(project);
        String[] columNames = Metric.getAllMetrics();
        List<String[]> data = new ArrayList<>();
        List<Release> releases;
        try {
            releases = repository.getReleases(repository.getFinalDate());
            LocalDate startRelease = LocalDate.of(1990, 10, 10);
            LocalDate endRelease = null;
            for(int i = 0; i < releases.size(); i++) {
                List<Release> rTemp = releases.subList(0, i + 1);
                Collections.reverse(rTemp);
                if (i == 2)
                    break;
                for (Release r : rTemp) {
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
                            for (ClassContent classContent : classContentList) {
                                if (classContent.getPath().equals(mu.getRepoClass().getPath())) {
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
                        for (Metric metric : Metric.values()) {
                            singleData[metric.ordinal()] = metrics.get(metric);
                        }
                        data.add(singleData);
                    }
                }
                CsvHandler.writeCsv("DATASET_" + repository.getProject() + "_RELEASE" + i+1, columNames, data);
                data.clear();
            }


        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            CsvHandler.writeCsv("DATASET_" + repository.getProject(), columNames, data);
        }
    }

    public static Set<ModFile> getBuggedFilesInRelease(Project project, Release release, LocalDate startRelease) throws IOException {
        Set<ModFile> modFilesSet = new LinkedHashSet<>();
        try {
            Repository repository = new Repository(project);
            LocalDate endRelease = LocalDate.parse(release.getReleaseDate());
            List<Commit> commits = repository.getCommits(startRelease, endRelease);
            List<Ticket> tickets = repository.getBugTickets(startRelease, endRelease);
            for (Ticket ticket : tickets) {
                for (Commit commit : commits) {
                    if(commit.getCommitDescr().getMessage().contains(ticket.getKey())) {
                        List<ModFile> modFiles = repository.getModFilesFromCommit(commit);
                        modFilesSet.addAll(modFiles);
                    }
                }
            }
            return modFilesSet;
        }catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<RepoFile> getFilesInRelease(Project project, Release release) throws IOException {
        Repository repository = new Repository(project);
        LocalDate endRelease = LocalDate.parse(release.getReleaseDate());
        String treeUrl = repository.getTreeUrlFromDate(endRelease);
        System.out.println(treeUrl + "?recursive=1");
        List<RepoFile> classes = repository.getClasses(treeUrl);
        return classes;
    }

    public static void statsOnReleases(Project project){
        Repository repository = new Repository(project);
        String[] columNames = Metric.getAllMetrics();
        List<String[]> data = new ArrayList<>();
        List<Release> releases;
        try {
            releases = repository.getReleases(repository.getFinalDate());
            LocalDate startRelease = LocalDate.of(1990, 10, 10);
            LocalDate endRelease = null;
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