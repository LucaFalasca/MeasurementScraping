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
