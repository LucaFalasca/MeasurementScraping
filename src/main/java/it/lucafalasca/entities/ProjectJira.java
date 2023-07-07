package it.lucafalasca.entities;

public class ProjectJira {

    String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {

            return "Project{" +
                    "key='" + key + '\'' +
                    '}';
    }
}
