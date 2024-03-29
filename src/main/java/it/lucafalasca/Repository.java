package it.lucafalasca;

import com.google.gson.Gson;
import it.lucafalasca.dao.GithubDao;
import it.lucafalasca.dao.JiraDao;
import it.lucafalasca.entities.*;
import it.lucafalasca.enumerations.Project;
import it.lucafalasca.util.JsonReader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repository {

    private GithubDao githubDao;
    private JiraDao jiraDao;
    private LocalDate finalDate;
    private List<CommitGithub> commitGithubs;
    private Project project;

    private Gson gson;

    private Map<Integer, List<ClassContent>> classContentHash;

    private List<Release> releases;

    public Repository(Project project) {
        this.project = project;
        this.githubDao = new GithubDao(project);
        this.jiraDao = new JiraDao(project);
        this.finalDate = project.getFinalDate();
        this.gson = new Gson();
        this.classContentHash = new HashMap<>();
    }

    public Project getProject() {
        return project;
    }

    public LocalDate getFinalDate() {
        return finalDate;
    }

    public List<RepoFile> getClasses(String shaTree) throws IOException {
        return githubDao.getClasses(shaTree);
    }

    public List<ClassContent> getClassesContent(int releaseNumber) throws IOException {
        if(!classContentHash.containsKey(releaseNumber)){
            JSONArray j = JsonReader.readJsonArrayFromFile("src/main/resources/json_files/" + project.toString() + "_classes/CLASSES_" + project.toString() + "_RELEASE_" + releaseNumber + ".json");
            List<ClassContent> classesContent = new ArrayList<>();
            for (int i = 0; i < j.length(); i++) {
                JSONObject classObject = j.getJSONObject(i);
                classesContent.add(gson.fromJson(classObject.toString(), ClassContent.class));
            }
            classContentHash.put(releaseNumber, classesContent);
        }
        return classContentHash.get(releaseNumber);
    }

    public String getTreeUrlFromDate(LocalDate date) throws IOException {
        return githubDao.getTreeUrlFromDate(date);
    }

    public List<CommitGithub> getCommits() throws IOException {
        if(commitGithubs == null)
            commitGithubs = githubDao.getCommits(null, finalDate);
        return commitGithubs;
    }

    public List<CommitGithub> getCommits(LocalDate startDate, LocalDate endDate) throws IOException {
        return githubDao.getCommits(startDate, endDate);
    }

    public List<ModFile> getModFilesFromCommit(CommitGithub commitGithub) throws IOException {
        return githubDao.getModFilesFromCommit(commitGithub);
    }

    public List<Release> getReleases() throws IOException {
        if(releases == null)
            releases = jiraDao.getReleases();
        return releases;
    }

    public List<Release> getReleases(LocalDate finalDate) throws IOException {
        if(releases == null)
            releases = jiraDao.getReleases(finalDate);
        return releases;
    }

    public List<Ticket> getBugTickets(LocalDate startRelease, LocalDate endRelease) throws IOException {
        return jiraDao.getBugTickets(startRelease, endRelease);
    }
}
