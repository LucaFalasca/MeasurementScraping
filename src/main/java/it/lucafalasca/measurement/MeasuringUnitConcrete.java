package it.lucafalasca.measurement;

import it.lucafalasca.entities.Release;
import it.lucafalasca.entities.RepoFile;
import it.lucafalasca.enumerations.Metric;
import it.lucafalasca.measurement.metrics.LocMetric;

import java.util.HashMap;
import java.util.Map;

public class MeasuringUnitConcrete implements MeasuringUnit{
    private Release release;
    private RepoFile repoClass;

    public MeasuringUnitConcrete(Release release, RepoFile repoClass){
        this.release = release;
        this.repoClass = repoClass;
    }

    @Override
    public String getValueFromMetric(Metric metric){
        switch (metric){
            case RELEASE:
                return String.valueOf(release.getVersionNumber());
            case CLASS:
                return repoClass.getPath();
            default:
                return null;
        }
    }

    @Override
    public Release getRelease() {
        return release;
    }

    @Override
    public RepoFile getRepoClass() {
        return repoClass;
    }

    @Override
    public MeasuringUnit addMetrics(Metric... metrics) {
        MeasuringUnit measuringUnit = this;
        for(Metric metric : metrics) {
            switch (metric) {
                case LOC:
                    return new LocMetric(measuringUnit);
                default:
                    return this;
            }
        }
        return measuringUnit;
    }

    @Override
    public void setMetricValue(Metric metric, String value) {
        switch (metric){
            case RELEASE:
                release.setVersionNumber(Integer.parseInt(value));
                break;
            case CLASS:
                repoClass.setPath(value);
                break;
            default:
                break;
        }
    }

    @Override
    public Map<Metric, String> getMetrics() {
        return getMetrics(Metric.values());
    }

    @Override
    public Map<Metric, String> getMetrics(Metric... metrics) {
        Map<Metric, String> metricsMap = new HashMap<>();
        for(Metric m: metrics) {
            metricsMap.put(m, getValueFromMetric(m));
        }
        return metricsMap;
    }
}
