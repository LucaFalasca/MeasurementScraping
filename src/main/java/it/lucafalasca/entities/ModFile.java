package it.lucafalasca.entities;

public class ModFile {

    private String filename;
    private String status;
    private int additions;
    private int deletions;
    private int changes;

    public String getFilename() {
        return filename;
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
}
