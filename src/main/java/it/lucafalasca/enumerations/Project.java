package it.lucafalasca.enumerations;

public enum Project {
    BOOKKEEPER("BOOKKEEPER"),
    AVRO("AVRO");

    private final String name;
    Project(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }
}
