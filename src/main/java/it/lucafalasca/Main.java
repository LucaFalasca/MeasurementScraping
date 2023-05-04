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
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Repository repository = new Repository(Project.BOOKKEEPER);
        /*
        List<Release> releases;
        try {
            List<RepoFile> classes = repository.getClasses("src/main/resources/classes.json");
            releases = repository.getReleases();
            for(int i = 0; i < releases.size(); i++) {
                Release currentRelease = releases.get(i);
                LocalDate startDate = LocalDate.parse(currentRelease.getReleaseDate());
                Release nextRelease = releases.get(i + 1);
                LocalDate endDate;
                if (i == releases.size() - 1) {
                    endDate = repository.getFinalDate();
                } else {
                    endDate = LocalDate.parse(nextRelease.getReleaseDate());
                }
                if (endDate.isAfter(repository.getFinalDate()) || endDate.isEqual(repository.getFinalDate())) {
                    break;
                }
                List<Commit> commits;
                commits = repository.getCommits(startDate, endDate);
                for (Commit commit : commits) {
                    List<ModFile> files = repository.getModFilesFromCommit(commit);
                    int founded = 0;
                    int notFounded = 0;
                    for (ModFile f : files) {
                        boolean b = false;
                        if(!f.getFilename().endsWith(".java") || f.getFilename().contains("Test")) {
                            continue;
                        }
                        for (RepoFile c : classes) {
                            if (c.getPath().equals(f.getFilename())) {
                                System.out.println(f.getFilename() + " found");
                                founded++;
                                b = true;
                                c.setAdditions(c.getAdditions() + f.getAdditions());
                                c.setDeletions(c.getDeletions() + f.getDeletions());
                            }
                        }
                        if(!b) {
                            System.out.println(f.getFilename() + " not found");
                            notFounded++;
                        }
                    }
                    if(founded + notFounded > 0) {
                        System.out.println("Founded: " + founded);
                        System.out.println("Not founded: " + notFounded);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        List<Release> releases;
        try{
            releases = repository.getReleases();
            for(Release r : releases) {
                System.out.println("Release: " + r.getVersionNumber());
                String treeUrl = repository.getTreeUrlFromDate(LocalDate.parse(r.getReleaseDate()));
                System.out.println(treeUrl + "?recursive=1");
                List<RepoFile> classes = repository.getClasses(treeUrl);
                System.out.println("Classes (" + classes.size() + ") ");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}