package it.lucafalasca.measurement;

import it.lucafalasca.entities.Release;
import it.lucafalasca.entities.RepoFile;
import it.lucafalasca.enumerations.Metric;
import it.lucafalasca.enumerations.Project;

import java.util.Map;

public interface MeasuringUnit<T> {

    public Project project = Project.BOOKKEEPER;
    public Map<Metric, String> getMetrics();

    public Release getRelease();

    public RepoFile getRepoClass();

    public Map<Metric, String> getMetrics(Metric... metrics);

    public String getValueFromMetric(Metric metric);

    public MeasuringUnit<T> addMetrics(Metric... metrics);

    public void setMetricValue(Metric metric, String value);

    public void calculateMetric(Metric metric, T input);

}
