package it.lucafalasca.entities;

import java.util.List;

public class Fields {

    private String created;

    private List<Release> fixVersions;
    private List<Release> versions;

    private IssueType issuetype;

    private String resolutiondate;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public List<Release> getFixVersions() {
        return fixVersions;
    }

    public void setFixVersions(List<Release> fixVersions) {
        this.fixVersions = fixVersions;
    }

    public List<Release> getVersions() {
        return versions;
    }

    public void setVersions(List<Release> versions) {
        this.versions = versions;
    }

    public IssueType getIssuetype() {
        return issuetype;
    }

    public void setIssuetype(IssueType issuetype) {
        this.issuetype = issuetype;
    }

    public String getResolutiondate() {
        return resolutiondate;
    }

    public void setResolutiondate(String resolutiondate) {
        this.resolutiondate = resolutiondate;
    }

    @Override
    public String toString() {
        return "Fields{\n\t" +
                "created='" + created + '\'' +
                ", fixVersions=" + fixVersions +
                ", versions=" + versions +
                ", issuetype=" + issuetype +
                ", resolutiondate=" + resolutiondate +
                '}';
    }
}
