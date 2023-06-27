package it.lucafalasca.enumerations;

public enum Metric {
    RELEASE("Release"),
    CLASS("Class"),
    CHURN("Churn"),
    LOC("LOC"),
    IF("IF"),
    IMPORT("Import"),
    CHANGES("Changes"),
    SEMICOLON("Semicolon"),
    COMMENT("Comment"),
    PUBLIC("Public"),
    PRIVATE("Private"),
    PROTECTED("Protected"),
    COMMIT("Commit"),

    BUGGYNESS("Buggyness");

    private final String name;

    Metric(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static String[] getAllMetrics(){
        String [] metrics = new String[Metric.values().length];
        for(Metric m : Metric.values()){
            metrics[m.ordinal()] = m.toString();
        }
        return metrics;
    }
}
