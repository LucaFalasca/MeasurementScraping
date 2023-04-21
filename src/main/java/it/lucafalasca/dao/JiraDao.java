package it.lucafalasca.dao;

import com.google.gson.Gson;
import it.lucafalasca.JsonReader;
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
    private final String BASE_URL = "https://issues.apache.org/jira/rest/api/2/project/";

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
    public List<Release> getReleases() throws IOException {
        String url = BASE_URL + project.toString() + "/version?orderBy=releaseDate&status=released";
        JSONObject json = JsonReader.readJsonFromUrl(url);
        JSONArray versions = json.getJSONArray("values");

        Gson gson = new Gson();
        List<Release> releases = new ArrayList<>();
        for(int i = 0; i < versions.length(); i++) {
            releases.add(gson.fromJson(versions.get(i).toString(), Release.class));
        }
        return releases;
    }

    /*
     * Get all releases of a project between two dates
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of releases
     */
    public List<Release> getReleases(LocalDate startDate, LocalDate endDate) {
        // TODO
        return null;
    }
}
