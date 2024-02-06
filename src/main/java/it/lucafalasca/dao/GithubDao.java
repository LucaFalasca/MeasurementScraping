package it.lucafalasca.dao;

import com.google.gson.Gson;
import it.lucafalasca.util.JsonReader;
import it.lucafalasca.entities.CommitGithub;
import it.lucafalasca.entities.RepoFile;
import it.lucafalasca.entities.ModFile;
import it.lucafalasca.enumerations.Project;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GithubDao {

    private Project project;
    private Gson gson;

    private static final String GITHUB_URL = "https://api.github.com/repos/apache/";

    private static final Logger logger = LoggerFactory.getLogger(GithubDao.class);

    public GithubDao(Project project) {
        this.project = project;
        gson = new Gson();
    }

    public List<CommitGithub> getCommits(LocalDate startDate, LocalDate endDate) throws IOException {
        String url;
        int page = 1;
        List<CommitGithub> commitGithubs = new ArrayList<>();
        JSONArray commitsJsonArray;
        long tTot = 0;
        do {
            long startTime1 = System.currentTimeMillis();
            url = GITHUB_URL + project.toString() + "/commits?per_page=100&page=" + page;
            logger.info(url);
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            if(startDate != null)
                sb.append("&since=").append(startDate.toString());
            if(endDate != null){
                sb.append("&until=").append(endDate.toString());
            }
            commitsJsonArray = JsonReader.readJsonArrayFromUrl(url, true);
            long endTime1 = System.currentTimeMillis();
            long elapsedTime1 = endTime1 - startTime1;
            long startTime2 = System.currentTimeMillis();
            for (int i = 0; i < commitsJsonArray.length(); i++) {
                JSONObject commitJsonObject = commitsJsonArray.getJSONObject(i);
                commitGithubs.add(gson.fromJson(commitJsonObject.toString(), CommitGithub.class));
            }
            long endTime2 = System.currentTimeMillis();
            long elapsedTime2 = endTime2 - startTime2;
            double totTime= (double)elapsedTime1 + elapsedTime2;
            tTot += (long) totTime;

            DecimalFormat df = new DecimalFormat("#.##");
            String formattedTime1 = df.format(elapsedTime1/totTime*100);
            String formattedTime2 = df.format(elapsedTime2/totTime*100);
            logger.info("Pagina: {}", page);
            logger.info("Tempo Query REST: {}ms ({}%)", elapsedTime1, formattedTime1);
            logger.info("Conversione da Json a Oggetti: {} ms({}%)", elapsedTime2, formattedTime2);
            logger.info("Tempo totale: {}", totTime);
            page++;
        }while(commitsJsonArray.length() == 100);
        logger.info("Tempo totale finale: {}", tTot);
        return commitGithubs;
    }

    public String getTreeUrlFromDate(LocalDate date) throws IOException {
        String url = GITHUB_URL + project.toString() + "/commits?per_page=1&until=" + date.toString();
        JSONArray commitJsonObject = JsonReader.readJsonArrayFromUrl(url, true);
        JSONObject commit = commitJsonObject.getJSONObject(0);
        JSONObject tree = commit.getJSONObject("commit").getJSONObject("tree");
        return tree.getString("url");
    }

    public List<ModFile> getModFilesFromCommit(CommitGithub commitGithub) throws IOException {
        String url = commitGithub.getUrl();

        JSONObject json = JsonReader.readJsonFromUrl(url, true);
        JSONArray modFilesJsonArray = json.getJSONArray("files");

        List<ModFile> modFiles = new ArrayList<>();
        for(int i = 0; i < modFilesJsonArray.length(); i++) {
            JSONObject modFileJsonObject = modFilesJsonArray.getJSONObject(i);
            ModFile modFile = gson.fromJson(modFileJsonObject.toString(), ModFile.class);
            if(modFile.getFilename().endsWith(".java") &&
                    !modFile.getFilename().contains("Test") &&
                    !modFile.getFilename().contains("package-info.java"))
                modFiles.add(modFile);
        }
        return modFiles;
    }

    public List<RepoFile> getClasses(String url) throws IOException {
        long startTime = System.currentTimeMillis();
        url += "?recursive=1";
        JSONObject treeJsonObject = JsonReader.readJsonFromUrl(url, true);
        JSONArray treeJsonArray = treeJsonObject.getJSONArray("tree");
        List<RepoFile> javaClasses = new ArrayList<>();
        logger.info("{}", treeJsonArray.length());
        for (int i = 0; i < treeJsonArray.length(); i++) {
            JSONObject fileJsonObject = treeJsonArray.getJSONObject(i);
            RepoFile file = gson.fromJson(fileJsonObject.toString(), RepoFile.class);
            if (file.getPath().endsWith(".java") &&
                    !file.getPath().contains("Test") &&
                    !file.getPath().contains("package-info.java")) {
                javaClasses.add(file);
            }
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        logger.info("Tempo totale: {}", elapsedTime);
        return javaClasses;
    }
}
