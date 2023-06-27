package it.lucafalasca.measurement;

import it.lucafalasca.entities.Release;
import it.lucafalasca.entities.RepoFile;
import it.lucafalasca.enumerations.Metric;
import it.lucafalasca.measurement.metrics.*;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class MeasuringUnitConcrete<T> implements MeasuringUnit<T>{
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
        MeasuringUnit<T> measuringUnit = this;
        for(Metric metric : metrics) {
            switch (metric) {
                case CHURN:
                    measuringUnit = new ChurnMetric(measuringUnit);
                    break;
                case IF:
                    measuringUnit =  new IfMetric(measuringUnit);
                    break;
                case IMPORT:
                    measuringUnit = new ImportMetric(measuringUnit);
                    break;
                case CHANGES:
                    measuringUnit = new ChangesMetric(measuringUnit);
                    break;
                case SEMICOLON:
                    measuringUnit = new SemicolonMetric(measuringUnit);
                    break;
                case COMMIT:
                    measuringUnit = new CommitMetric(measuringUnit);
                    break;
                case LOC:
                    measuringUnit = new LocMetric(measuringUnit);
                    break;
                case COMMENT:
                    measuringUnit = new CommentMetric(measuringUnit);
                    break;
                case PUBLIC:
                    measuringUnit = new PublicMetric(measuringUnit);
                    break;
                case PRIVATE:
                    measuringUnit = new PrivateMetric(measuringUnit);
                    break;
                case PROTECTED:
                    measuringUnit = new ProtectedMetric(measuringUnit);
                    break;
                case BUGGYNESS:
                    measuringUnit = new BuggynessMetric(measuringUnit);
                    break;
                default:
                    break;
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
    public void calculateMetric(Metric metric, T input) {
        // do nothing
    }

    @Override
    public Map<Metric, String> getMetrics() {
        return getMetrics(Metric.values());
    }

    @Override
    public Map<Metric, String> getMetrics(Metric... metrics) {
        Map<Metric, String> metricsMap = new EnumMap<>(Metric.class);
        for(Metric m: metrics) {
            metricsMap.put(m, getValueFromMetric(m));
        }
        return metricsMap;
    }

}
