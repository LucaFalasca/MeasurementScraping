package it.lucafalasca.dao;

import com.google.gson.Gson;
import it.lucafalasca.entities.Ticket;
import it.lucafalasca.util.JsonReader;
import it.lucafalasca.entities.Release;
import it.lucafalasca.enumerations.Project;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JiraDao {

    /*
     * The project we want to take data from
     */
    private final Project project;
    /*
     * Base URL for Jira API
     */
    private static final String BASE_URL = "https://issues.apache.org/jira/rest/api/2/project/";

    /*
     * Constructor
     * @param project the project we want to take data from
     */
    public JiraDao(Project project) {
        this.project = project;
    }

    /*
     * Get all releases of a project
     * @return a list of releases
     */
    public List<Release> getReleases(LocalDate finalDate) throws IOException {
        String url = BASE_URL + project.toString() + "/version?orderBy=releaseDate&status=released";
        JSONObject json = JsonReader.readJsonFromUrl(url, false);
        assert json != null;
        JSONArray versions = json.getJSONArray("values");

        Gson gson = new Gson();
        List<Release> releases = new ArrayList<>();
        for(int i = 0; i < versions.length(); i++) {
            Release release = gson.fromJson(versions.get(i).toString(), Release.class);
            if(!(release.getReleaseDate() == null || release.getReleaseDate().equals("null"))){
                release.setVersionNumber(i + 1);
                releases.add(release);
                if(finalDate != null && LocalDate.parse(release.getReleaseDate()).isAfter(finalDate.minusDays(1))){
                    release.setReleaseDate(finalDate.toString());
                    break;
                }
            }
        }
        return releases;
    }

    public List<Release> getReleases() throws IOException {
        return getReleases(null);
    }


    public List<Ticket> getBugTickets(LocalDate startRelease, LocalDate endRelease) throws IOException {
        String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%27" + project.toString() +"%27AND'issueType'='Bug'AND('status'='closed'OR'status'='resolved')AND'resolution'='fixed'&fields=key&maxResults=1000&fields=fixVersions,issuetype,resolutiondate,created,versions";
        System.out.println(url);
        JSONObject json = JsonReader.readJsonFromUrl(url, false);
        assert json != null;
        JSONArray issues = json.getJSONArray("issues");

        Gson gson = new Gson();
        List<Ticket> tickets = new ArrayList<>();
        for(int i = 0; i < issues.length(); i++) {
            Ticket ticket = gson.fromJson(issues.get(i).toString(), Ticket.class);
            LocalDate resolutionDate = LocalDate.parse(ticket.getFields().getResolutiondate().substring(0, 10));
            if (resolutionDate.isAfter(startRelease) && resolutionDate.isBefore(endRelease))
                tickets.add(ticket);


        }
        return tickets;
    }

    public static List<Ticket> getBugTickets(Project[] projects) throws IOException {
        String projectsString = "";
        for(Project project : projects){
            projectsString += project.toString() + ",";
        }
        projectsString = projectsString.substring(0, projectsString.length() - 1);

        int startAt = 0;
        String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project%20in%20(" + projectsString + ")%20AND%20issuetype%20=%20Bug%20AND%20status%20in%20(Resolved,%20Closed)%20AND%20resolution%20in%20(Fixed,%20Done,%20Resolved)%20AND%20affectedVersion%20in%20releasedVersions()%20AND%20fixVersion%20in%20releasedVersions()%20%20&maxResults=1000&fields=fixVersions,issuetype,created,versions,project&startAt=" + startAt;
        System.out.println(url);
        JSONObject json = JsonReader.readJsonFromUrl(url, false);
        //System.out.println(json);
        assert json != null;
        String total = json.get("total").toString();

        JSONArray issues = json.getJSONArray("issues");

        Gson gson = new Gson();
        List<Ticket> tickets = new ArrayList<>();
        for(int i = 0; i < issues.length(); i++) {
            Ticket ticket = gson.fromJson(issues.get(i).toString(), Ticket.class);
            tickets.add(ticket);
        }

        for (int i = 1000; i < Integer.parseInt(total); i += 1000){
            startAt = i;
            json = JsonReader.readJsonFromUrl(url, false);
            issues = json.getJSONArray("issues");
            for(int j = 0; j < issues.length(); j++) {
                Ticket ticket = gson.fromJson(issues.get(j).toString(), Ticket.class);
                tickets.add(ticket);
            }
        }
        //System.out.println(tickets);
        return tickets;


    }
}
