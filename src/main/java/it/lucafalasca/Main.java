package it.lucafalasca;

import it.lucafalasca.dao.JiraDao;
import it.lucafalasca.entities.Release;
import it.lucafalasca.enumerations.Project;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        JiraDao jiraDao = new JiraDao(Project.BOOKKEEPER);
        try {
            List<Release> releases = jiraDao.getReleases();
            System.out.println(releases);
            System.out.println(releases.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}