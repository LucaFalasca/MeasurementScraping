package it.lucafalasca.enumerations;

public enum Metric {
    RELEASE("Release"),
    CLASS("Class"),
    LOC("LOC");

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
