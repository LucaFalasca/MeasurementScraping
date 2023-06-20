package it.lucafalasca.dao;

import com.google.gson.Gson;
import it.lucafalasca.util.JsonReader;
import it.lucafalasca.entities.Commit;
import it.lucafalasca.entities.RepoFile;
import it.lucafalasca.entities.ModFile;
import it.lucafalasca.enumerations.Project;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GithubDao {

    private Project project;
    private Gson gson;

    private final String GITHUB_URL = "https://api.github.com/repos/apache/";

    public GithubDao(Project project) {
        this.project = project;
        gson = new Gson();
    }

    public List<Commit> getCommits(LocalDate startDate, LocalDate endDate) throws IOException {
        String url;
        int page = 1;
        List<Commit> commits = new ArrayList<>();
        JSONArray commitsJsonArray;
        long tTot = 0;
        do {
            long startTime1 = System.currentTimeMillis();
            url = GITHUB_URL + project.toString() + "/commits?per_page=100&page=" + page;
            System.out.println(url);
            if(startDate != null)
                url += "&since=" + startDate.toString();
            if(endDate != null){
                url += "&until=" + endDate.toString();
            }
            commitsJsonArray = JsonReader.readJsonArrayFromUrl(url, true);
            long endTime1 = System.currentTimeMillis();
            long elapsedTime1 = endTime1 - startTime1;
            long startTime2 = System.currentTimeMillis();
            for (int i = 0; i < commitsJsonArray.length(); i++) {
                JSONObject commitJsonObject = commitsJsonArray.getJSONObject(i);
                commits.add(gson.fromJson(commitJsonObject.toString(), Commit.class));
            }
            long endTime2 = System.currentTimeMillis();
            long elapsedTime2 = endTime2 - startTime2;
            double totTime= elapsedTime1 + elapsedTime2;
            tTot += totTime;

            DecimalFormat df = new DecimalFormat("#.##");

            System.out.println("Pagina: " + page);
            System.out.println("Tempo Query REST: " + elapsedTime1 + "ms (" + df.format(elapsedTime1/totTime*100) + "%)");
            System.out.println("Conversione da Json a Oggetti: " + elapsedTime2 + " ms(" + df.format(elapsedTime2/totTime*100) + "%)");
            System.out.println("Tempo totale: " + totTime);
            page++;
        }while(commitsJsonArray.length() == 100);
        System.out.println("Tempo totale finale: " + tTot);
        return commits;
    }

    public String getTreeUrlFromDate(LocalDate date) throws IOException {
        String url = GITHUB_URL + project.toString() + "/commits?per_page=1&until=" + date.toString();
        JSONArray commitJsonObject = JsonReader.readJsonArrayFromUrl(url, true);
        JSONObject commit = commitJsonObject.getJSONObject(0);
        JSONObject tree = commit.getJSONObject("commit").getJSONObject("tree");
        return tree.getString("url");
    }

    public List<ModFile> getModFilesFromCommit(Commit commit) throws IOException {
        String url = commit.getUrl();

        JSONObject json = JsonReader.readJsonFromUrl(url, true);
        JSONArray modFilesJsonArray = json.getJSONArray("files");

        List<ModFile> modFiles = new ArrayList<>();
        for(int i = 0; i < modFilesJsonArray.length(); i++) {
            JSONObject modFileJsonObject = modFilesJsonArray.getJSONObject(i);
            modFiles.add(gson.fromJson(modFileJsonObject.toString(), ModFile.class));
        }
        return modFiles;
    }

    public List<RepoFile> getClasses(String url) throws IOException {
        //String url = GITHUB_URL + project.toString() + "/git/trees/" + shaTree + "?recursive=1";
        long startTime = System.currentTimeMillis();
        url += "?recursive=1";
        JSONObject treeJsonObject = JsonReader.readJsonFromUrl(url, true);
        JSONArray treeJsonArray = treeJsonObject.getJSONArray("tree");
        List<RepoFile> javaClasses = new ArrayList<>();
        System.out.println(treeJsonArray.length());
        for (int i = 0; i < treeJsonArray.length(); i++) {
            JSONObject fileJsonObject = treeJsonArray.getJSONObject(i);
            RepoFile file = gson.fromJson(fileJsonObject.toString(), RepoFile.class);
            if(file.getType().equals("tree")) {
                //System.out.println("DIR:" + file.getPath());
                //javaClasses.addAll(getClasses(file.getUrl()));

            }
            else if (file.getPath().endsWith(".java") &&
                    !file.getPath().contains("Test") &&
                    !file.getPath().contains("package-info.java")) {
                //System.out.println("Class Found:" + file.getPath());
                javaClasses.add(file);
            }
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Tempo totale: " + elapsedTime);
        return javaClasses;
    }

    /*
    private List<RepoFile> getClassesFromUrl(String url) throws IOException {
        JSONArray filesJsonArray = JsonReader.readJsonArrayFromUrl(url, true);
        List<RepoFile> javaClasses = new ArrayList<>();

        for (int i = 0; i < filesJsonArray.length(); i++) {
            JSONObject fileJsonObject = filesJsonArray.getJSONObject(i);
            RepoFile file = gson.fromJson(fileJsonObject.toString(), RepoFile.class);
            if(file.getType().equals("tree")) {
                //System.out.println("DIR:" + file.getName());
                javaClasses.addAll(getClasses(file.getUrl()));
            }
            else if (file.getName().endsWith(".java") &&
                    !file.getName().contains("Test") &&
                    !file.getName().contains("package-info.java")) {
                //System.out.println("Class Found:" + file.getName());
                javaClasses.add(file);
            }
        }
        return javaClasses;
    }
     */

}
