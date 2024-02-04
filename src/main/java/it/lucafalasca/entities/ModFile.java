package it.lucafalasca.entities;

import java.util.*;

public class ModFile {

    private String filename;
    private String status;
    private int additions;
    private int deletions;
    private int changes;
    private Set<Integer> affectedReleases;

    public ModFile(String filename, String status, int additions, int deletions, int changes) {
        this.filename = filename;
        this.status = status;
        this.additions = additions;
        this.deletions = deletions;
        this.changes = changes;
        this.affectedReleases = new LinkedHashSet<>();
    }



    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getStatus() {
        return status;
    }

    public int getAdditions() {
        return additions;
    }

    public int getDeletions() {
        return deletions;
    }

    public int getChanges() {
        return changes;
    }

    public Set<Integer> getAffectedReleases() {
        return affectedReleases;
    }

    public void setAffectedReleases(Set<Integer> affectedReleases) {
        this.affectedReleases = affectedReleases;
    }

    public void addAffectedRelease(int release) {
        affectedReleases.add(release);
    }

    public void addAffectedReleases(Set<Integer> releases) {
        affectedReleases.addAll(releases);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ModFile)) {
            return false;
        }
        return filename.equals(((ModFile) o).getFilename());
    }

    @Override
    public String toString() {
        return filename;
    }

    @Override
    public int hashCode() {
        return filename.hashCode();
    }
}
