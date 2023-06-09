package it.lucafalasca.cold_start;

import it.lucafalasca.Repository;
import it.lucafalasca.dao.JiraDao;
import it.lucafalasca.entities.Fields;
import it.lucafalasca.entities.Release;
import it.lucafalasca.entities.Ticket;
import it.lucafalasca.enumerations.Project;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColdStart {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    public static void coldStart() throws IOException {
        JiraDao jiraDao = new JiraDao(Project.BOOKKEEPER);
        Project[] projects = new Project[]{Project.BOOKKEEPER, Project.AVRO};

        Map<Project, List<Release>> ProjectReleases = new HashMap<>();
        for(Project project : projects){
            Repository repository = new Repository(project);
            List<Release> releases = repository.getReleases();
            ProjectReleases.put(project, releases);
        }

        List<Ticket> tickets = jiraDao.getBugTickets(projects);
        System.out.println("Number of tickets: " + tickets.size());
        int c = 0;
        float sum = 0;
        for (Ticket ticket : tickets) {

            Fields fields = ticket.getFields();

            Project project = Project.getProjectByName(fields.getProject().getKey());


            List<Release> fixVersions = fields.getFixVersions();
            Release fixVersion = maxVersion(fixVersions);
            if(fixVersion == null){
                continue;
            }
            fixVersion.setVersionNumber(getVersionNumberFromRelease(ProjectReleases.get(project), fixVersion));

            List<Release> affectedVersions = fields.getVersions();
            Release injectedVersion = minVersion(affectedVersions);
            if(injectedVersion == null){
                continue;
            }
            injectedVersion.setVersionNumber(getVersionNumberFromRelease(ProjectReleases.get(project), injectedVersion));



            Release openingVersion = getReleaseFromDate(ProjectReleases.get(project), LocalDate.parse(fields.getCreated().substring(0, 10)));

            int fixVersionNumber = fixVersion.getVersionNumber();
            int injectedVersionNumber = injectedVersion.getVersionNumber();
            int openingVersionNumber = openingVersion.getVersionNumber();


            if(!(fixVersionNumber <= openingVersionNumber || fixVersionNumber <= injectedVersionNumber || injectedVersionNumber >= openingVersionNumber)){
                c++;
                float p = (fixVersionNumber - injectedVersionNumber)/(fixVersionNumber - openingVersionNumber);
                sum += p;
            }
        }
        System.out.println("Number of tickets with right versions: " + c);
        System.out.println("Number of tickets with wrong versions: " + (tickets.size() - c));
        float mean = sum/c;
        System.out.println("Mean: " + mean);
    }

    private static int getVersionNumberFromRelease(List<Release> releases, Release r) {
        for(Release release : releases){
            if(release.equals(r)){
                return release.getVersionNumber();
            }
        }
        return -1;
    }

    private static Release getReleaseFromDate(List<Release> releases, LocalDate releaseDate) {
        for(int i = 0; i < releases.size() - 1; i++){
            LocalDate start = LocalDate.parse(releases.get(i).getReleaseDate());
            LocalDate end = LocalDate.parse(releases.get(i+1).getReleaseDate());
            if(releaseDate.isAfter(start) && releaseDate.isBefore(end)){
                return releases.get(i);
            }
        }
        return releases.get(releases.size() - 1);
    }

    private static Release minVersion(List<Release> releases){
        int i = 0;
        Release min = releases.get(i);
        if(min.getReleaseDate() == null){
            if(i < releases.size() - 1){
                i++;
                min = releases.get(i);
            }
            else {
                return null;
            }
        }
        LocalDate minDate = LocalDate.parse(min.getReleaseDate());
        for(Release release : releases){
            if(release.getReleaseDate() == null){
                continue;
            }
            LocalDate releaseDate = LocalDate.parse(release.getReleaseDate());
            if(releaseDate.isBefore(minDate)){
                min = release;
                minDate = releaseDate;
            }
        }
        return min;
    }

    private static Release maxVersion(List<Release> releases){
        int i = 0;
        Release max = releases.get(i);
        if(max.getReleaseDate() == null){
            if(i < releases.size() - 1){
                i++;
                max = releases.get(i);
            }
            else {
                return null;
            }
        }
        LocalDate maxDate = LocalDate.parse(max.getReleaseDate());
        for(Release release : releases){
            if(release.getReleaseDate() == null){
                continue;
            }
            LocalDate releaseDate = LocalDate.parse(release.getReleaseDate());
            if(releaseDate.isAfter(maxDate)){
                max = release;
                maxDate = releaseDate;
            }
        }
        return max;
    }


}
