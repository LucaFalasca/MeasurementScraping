package it.lucafalasca;

import com.google.gson.Gson;
import it.lucafalasca.dao.GithubDao;
import it.lucafalasca.dao.JiraDao;
import it.lucafalasca.entities.Commit;
import it.lucafalasca.entities.ModFile;
import it.lucafalasca.entities.Release;
import it.lucafalasca.entities.RepoFile;
import it.lucafalasca.enumerations.Project;
import org.json.JSONArray;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Repository {

    private GithubDao githubDao;
    private JiraDao jiraDao;
    private LocalDate finalDate;
    private List<Commit> commits;

    private List<Release> releases;

    public Repository(Project project) {
        this.githubDao = new GithubDao(project);
        this.jiraDao = new JiraDao(project);
        this.finalDate = project.getFinalDate();
    }

    public LocalDate getFinalDate() {
        return finalDate;
    }

    public List<RepoFile> getClasses(String shaTree) throws IOException {
        return githubDao.getClasses(shaTree);
    }

    public String getTreeUrlFromDate(LocalDate date) throws IOException {
        return githubDao.getTreeUrlFromDate(date);
    }

    /*
    public List<RepoFile> getClasses(String pathJsonFile) throws IOException {
        if(classes == null) {
            Gson gson = new Gson();
            classes = new ArrayList<>();
            JSONArray jsonArray = JsonReader.readJsonArrayFromFile(pathJsonFile);
            for (int i = 0; i < jsonArray.length(); i++) {
                String classJson = jsonArray.getJSONObject(i).toString();
                RepoFile c = gson.fromJson(classJson, RepoFile.class);
                classes.add(c);
            }
        }
        return classes;
    }
     */

    public List<Commit> getCommits() throws IOException {
        if(commits == null)
            commits = githubDao.getCommits(null, finalDate);
        return commits;
    }

    public List<Commit> getCommits(LocalDate startDate, LocalDate endDate) throws IOException {
        return githubDao.getCommits(startDate, endDate);
    }

    public List<ModFile> getModFilesFromCommit(Commit commit) throws IOException {
        return githubDao.getModFilesFromCommit(commit);
    }

    public List<Release> getReleases() throws IOException {
        if(releases == null)
            releases = jiraDao.getReleases();
        return releases;
    }

}
