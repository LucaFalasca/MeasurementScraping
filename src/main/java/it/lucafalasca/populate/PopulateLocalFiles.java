package it.lucafalasca.populate;

import it.lucafalasca.Repository;
import it.lucafalasca.cold_start.ColdStart;
import it.lucafalasca.entities.*;
import it.lucafalasca.enumerations.*;
import it.lucafalasca.measurement.MeasuringUnit;
import it.lucafalasca.measurement.MeasuringUnitConcrete;
import it.lucafalasca.util.ArffMaker;
import it.lucafalasca.util.CsvHandler;
import it.lucafalasca.util.JsonMerger;
import it.lucafalasca.util.JsonReader;
import it.lucafalasca.weka.Classification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class PopulateLocalFiles {

    static Logger logger = LoggerFactory.getLogger(PopulateLocalFiles.class);
    private static final String RELEASE_STRING = "_RELEASE_";
    private static final String RECURSIVE_STRING = "?recursive=1";

    private PopulateLocalFiles(){

    }


    public static void populateLocalJsonClasses(Project project) throws IOException {
        Repository repository = new Repository(project);
        List<Release> releases;

        releases = repository.getReleases(repository.getFinalDate());
        for (Release r : releases) {
            logger.info("Release: {}", r.getVersionNumber());
            logger.info("Release Date {}", r.getReleaseDate());
            LocalDate date = LocalDate.parse(r.getReleaseDate());
            String treeUrl = repository.getTreeUrlFromDate(date);
            logger.info("{}{}", RECURSIVE_STRING, treeUrl);
            List<RepoFile> classes = repository.getClasses(treeUrl);
            List<String> jsonClasses = new ArrayList<>();

            for (RepoFile c : classes) {
                String jsonClass = JsonReader.readJsonFromUrl(c.getUrl(), true).toString();
                jsonClass = JsonMerger.addAttrInJsonObjectString(jsonClass, "path", c.getPath());
                jsonClasses.add(jsonClass);
            }
            String jsonRelease = JsonMerger.mergeStringInJsonArrayString(jsonClasses);
            JsonMerger.writeStringOnFile(repository.getProject() + "_classes/" + "CLASSES_" +  repository.getProject() + RELEASE_STRING + r.getVersionNumber(), jsonRelease);
        }
    }


    public static void populateDatasets(Project project) throws IOException {
        Repository repository = new Repository(project);
        String[] columNames = Metric.getAllMetrics();
        List<String[]> data = new ArrayList<>();
        List<Release> releases;
        LocalDateTime now = LocalDateTime.now();
        try {

            releases = repository.getReleases(repository.getFinalDate());
            LocalDate startRelease = LocalDate.of(1990, 10, 10);
            LocalDate endRelease = null;
            List<MeasuringUnit<Object>>[] measuringUnitsOnRelease = new List[releases.size()];
            for (Release r : releases) {
                logger.info("Release: {}", r.getVersionNumber());
                logger.info("Release Date {}", r.getReleaseDate());
                LocalDate date = LocalDate.parse(r.getReleaseDate());
                String treeUrl = repository.getTreeUrlFromDate(date);
                logger.info("{}{}", RECURSIVE_STRING, treeUrl);
                List<RepoFile> classes = repository.getClasses(treeUrl);

                List<MeasuringUnit<Object>> measuringUnits = new ArrayList<>();
                for (RepoFile c : classes) {
                    MeasuringUnit<Object> mu = new MeasuringUnitConcrete<>(r, c);
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
                logger.info("Classes ({}) ", classes.size());
                endRelease = LocalDate.parse(r.getReleaseDate());
                List<Commit> commits = repository.getCommits(startRelease, endRelease);

                assignMetrics(commits, repository, measuringUnits, r);

                measuringUnitsOnRelease[r.getVersionNumber() - 1] = measuringUnits;
                Set<ModFile> buggedFiles = getBuggedFilesInRelease(project, r, startRelease);

                if(r.getVersionNumber() > 1) {
                    assignBuggyness(buggedFiles, measuringUnitsOnRelease, r);
                    generateTrainDatasets(repository, releases, measuringUnitsOnRelease, r, now, columNames, data);
                    generateTestDatasets(repository, releases, measuringUnitsOnRelease, r, now, columNames, data);
                    startRelease = endRelease;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void assignMetrics(List<Commit> commits, Repository repository, List<MeasuringUnit<Object>> measuringUnits, Release r) throws IOException {
        for (Commit commit : commits) {
            List<ModFile> modFiles = repository.getModFilesFromCommit(commit);
            List<ClassContent> classContentList = repository.getClassesContent(r.getVersionNumber());
            for (MeasuringUnit<Object> mu : measuringUnits) {
                calculateFileMetrics(mu, modFiles);
                calculateClassMetrics(mu, classContentList);
            }
        }

    }

    private static void calculateFileMetrics(MeasuringUnit<Object> mu, List<ModFile> modFiles){
        for (ModFile modFile : modFiles) {
            if (modFile.getFilename().equals(mu.getRepoClass().getPath())) {
                mu.calculateMetric(Metric.CHURN, modFile);
                mu.calculateMetric(Metric.CHANGES, modFile);
                mu.calculateMetric(Metric.COMMIT, 1);
            }
        }
    }

    private static void calculateClassMetrics(MeasuringUnit<Object> mu, List<ClassContent> classContentList){
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

    private static void assignBuggyness(Set<ModFile> buggedFiles, List<MeasuringUnit<Object>>[] measuringUnitsOnRelease, Release r) {
        for (int i = r.getVersionNumber() - 1; i >= 0; i--) {
            for (MeasuringUnit<Object> mu : measuringUnitsOnRelease[i]) {
                for (ModFile modFile : buggedFiles) {
                    Set<Integer> affectedReleases = modFile.getAffectedReleases();
                    if (affectedReleases.contains(i + 1) &&
                            modFile.getFilename().equals(mu.getRepoClass().getPath())) {
                        mu.calculateMetric(Metric.BUGGYNESS, true);
                    }
                }
            }
        }
    }

    private static void generateTrainDatasets(Repository repository, List<Release> releases, List<MeasuringUnit<Object>>[] measuringUnitsOnRelease, Release r, LocalDateTime now, String[] columNames, List<String[]> data) throws IOException {
        if(r.getVersionNumber() < releases.size() / 2 + 1 + 1) {
            for (int i = 0; i < r.getVersionNumber() - 1; i++) {
                for (MeasuringUnit<Object> mu : measuringUnitsOnRelease[i]) {
                    Map<Metric, String> metrics = mu.getMetrics();
                    String[] singleData = new String[Metric.values().length];
                    for (Metric metric : Metric.values()) {
                        singleData[metric.ordinal()] = metrics.get(metric);
                    }
                    data.add(singleData);
                }
            }
            String path = CsvHandler.writeCsv("DATASET_" + repository.getProject() + RELEASE_STRING + r.getVersionNumber() + "_TRAIN", columNames, data, now);
            ArffMaker.csvToArff(path);
            data.clear();
        }
    }

    private static void generateTestDatasets(Repository repository, List<Release> releases, List<MeasuringUnit<Object>>[] measuringUnitsOnRelease, Release r, LocalDateTime now, String[] columNames, List<String[]> data) throws IOException {
        if(r.getVersionNumber() == releases.size()){
            //Testing set
            for (int i = 1; i < (releases.size()) / 2 + 1 + 1; i++) {
                for (MeasuringUnit<Object> mu : measuringUnitsOnRelease[i]) {
                    Map<Metric, String> metrics = mu.getMetrics();
                    String[] singleData = new String[Metric.values().length];
                    for (Metric metric : Metric.values()) {
                        singleData[metric.ordinal()] = metrics.get(metric);
                    }
                    data.add(singleData);
                }
                String path = CsvHandler.writeCsv("DATASET_" + repository.getProject() + RELEASE_STRING + releases.get(i).getVersionNumber() + "_TEST", columNames, data, now);
                ArffMaker.csvToArff(path);
                data.clear();
            }
        }
    }

    public static Set<ModFile> getBuggedFilesInRelease(Project project, Release release, LocalDate startRelease) throws IOException {
        Set<ModFile> modFilesSet = new LinkedHashSet<>();
        try {
            Repository repository = new Repository(project);
            LocalDate endRelease = LocalDate.parse(release.getReleaseDate());
            List<Commit> commits = repository.getCommits(startRelease, endRelease);
            List<Ticket> tickets = repository.getBugTickets(startRelease, endRelease);
            List<Release> releases = repository.getReleases(repository.getFinalDate());
            for (Ticket ticket : tickets) {
                Fields fields = ticket.getFields();
                List<Release> af = fields.getVersions();

                int injectedVersionNumber = 0;
                int fixVersionNumber = release.getVersionNumber();
                if(af == null || af.isEmpty() || ColdStart.minVersion(af) == null){
                    //With cold start
                    Release openingVersion = ColdStart.getReleaseFromDate(releases, LocalDate.parse(fields.getCreated().substring(0, 10)));
                    int openingVersionNumber = openingVersion.getVersionNumber();
                    injectedVersionNumber = ColdStart.calculateInjectedVersionNumberFromP(1.256, fixVersionNumber, openingVersionNumber);
                }
                else {
                    Release injectedVersion = ColdStart.minVersion(af);
                    injectedVersion.setVersionNumber(ColdStart.getVersionNumberFromRelease(releases, injectedVersion));
                    injectedVersionNumber = injectedVersion.getVersionNumber();
                }
                Set<Integer> affectedVersions = new LinkedHashSet<>();
                for(int i = injectedVersionNumber; i <= fixVersionNumber; i++){
                    affectedVersions.add(i);
                }
                addAffectedReleases(affectedVersions, modFilesSet, commits, repository, ticket);
            }
            return modFilesSet;
        }catch(IOException e) {
            return new LinkedHashSet<>();
        }
    }

    private static void addAffectedReleases(Set<Integer> affectedVersions, Set<ModFile> modFilesSet, List<Commit> commits, Repository repository, Ticket ticket) throws IOException {
        for (Commit commit : commits) {
            if(commit.getCommitDescr().getMessage().contains(ticket.getKey())) {
                List<ModFile> modFiles = repository.getModFilesFromCommit(commit);
                for(ModFile modFile : modFiles) {
                    modFile.setAffectedReleases(affectedVersions);
                }
                modFilesSet.addAll(modFiles);
            }
        }
    }

    public static List<RepoFile> getFilesInRelease(Project project, Release release) throws IOException {
        Repository repository = new Repository(project);
        LocalDate endRelease = LocalDate.parse(release.getReleaseDate());
        String treeUrl = repository.getTreeUrlFromDate(endRelease);
        logger.info("{}{}", RECURSIVE_STRING, treeUrl);
        return repository.getClasses(treeUrl);
    }

    public static void statsOnReleases(Project project){
        Repository repository = new Repository(project);
        List<Release> releases;
        try {
            releases = repository.getReleases(repository.getFinalDate());
            LocalDate startRelease;
            for(int i = 0; i < releases.size() - 1; i++) {
                Release r = releases.get(i);
                Release r2 = releases.get(i + 1);
                startRelease = LocalDate.parse(r.getReleaseDate());
                List<RepoFile> files = PopulateLocalFiles.getFilesInRelease(project, r);
                Set<ModFile> files2 = PopulateLocalFiles.getBuggedFilesInRelease(project, r2, startRelease);
                logger.info("---------RELEASE  {} -> {} ---------", r.getVersionNumber(), r2.getVersionNumber());
                logger.info("Classi Fixate nella release {}: {}", r2.getVersionNumber(), files2.size());

                if (files2.isEmpty()) {
                    logger.info("Nessuna classe Fixata nella release {}", r2.getVersionNumber());
                }
                else{
                    int[] ret =countClasses(files, files2);
                    int classesSurvivors = ret[0];
                    int renamedClasses = ret[1];
                    logger.info("Classi sopravvissute: {}({}%)",classesSurvivors, classesSurvivors / files2.size());
                    logger.info("Classi rinominate: {}({}%)", renamedClasses, renamedClasses / files2.size());
                    logger.info("Classi fixate nella release: {}({}%)", (files2.size() - classesSurvivors - renamedClasses), (files2.size() - classesSurvivors - renamedClasses) / files2.size());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private static int[] countClasses(List<RepoFile> files, Set<ModFile> files2) {
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
        return new int[]{classesSurvivors, renamedClasses};
    }

    public static void doExperiments(Project project) throws Exception {
        Classifier classifier;
        DataSource testSource;
        DataSource trainSource;
        Instances train;
        Instances test;

        int release = 2;
        String[] columNames = new String[]{"Project", "Release", "Experiment", "Classifier", "AUC", "Precision", "Recall", "Kappa"};
        List<String[]> data = new ArrayList<>();
        while(true){
            String basePath = "src/main/resources/arff_files/ARFF_DATASET_";
            String pathTrain = basePath + project + RELEASE_STRING + release + "_TRAIN.arff";
            String pathTest = basePath + project + RELEASE_STRING + release + "_TEST.arff";
            if(!(Files.exists(Path.of(pathTrain)) && Files.exists(Path.of(pathTest)))){
                break;
            }
            trainSource = new DataSource(pathTrain);
            testSource = new DataSource(pathTest);
            train = trainSource.getDataSet();
            test = testSource.getDataSet();

            train.setClassIndex(train.numAttributes() - 1);
            test.setClassIndex(test.numAttributes() - 1);

            for (Experiment experiment : Experiment.values()) {
                for (ClassifierSupported classifierSupported : ClassifierSupported.values()) {
                    classifier = classifierSupported.getClassifier();
                    Map<EvaluationMetric, Double> res = Classification.classify(train, test, classifier, experiment);
                    logger.info("Release: {}  Experiment: {} Classifier: {}", release, experiment, classifierSupported);
                    logger.info("AUC: {}", res.get(EvaluationMetric.AUC));
                    logger.info("Precision: {}", res.get(EvaluationMetric.PRECISION));
                    logger.info("Recall: {}", + res.get(EvaluationMetric.RECALL));
                    logger.info("Kappa: {}", res.get(EvaluationMetric.KAPPA));
                    logger.info("-------------------------------------------------");
                    String[] singleData = new String[columNames.length];
                    singleData[0] = project.toString();
                    singleData[1] = String.valueOf(release);
                    singleData[2] = experiment.toString();
                    singleData[3] = classifierSupported.toString();
                    singleData[4] = String.valueOf(res.get(EvaluationMetric.AUC));
                    singleData[5] = String.valueOf(res.get(EvaluationMetric.PRECISION));
                    singleData[6] = String.valueOf(res.get(EvaluationMetric.RECALL));
                    singleData[7] = String.valueOf(res.get(EvaluationMetric.KAPPA));
                    data.add(singleData);
                }
            }


            release++;
        }
        CsvHandler.writeCsv("EXPERIMENTS_" + project.toString(), columNames, data);
    }
}