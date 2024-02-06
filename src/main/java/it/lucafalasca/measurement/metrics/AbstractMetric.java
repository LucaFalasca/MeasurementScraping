package it.lucafalasca.measurement.metrics;

import it.lucafalasca.Repository;
import it.lucafalasca.entities.Release;
import it.lucafalasca.entities.RepoFile;
import it.lucafalasca.enumerations.Metric;
import it.lucafalasca.measurement.MeasuringUnit;

import java.util.Map;

public abstract class AbstractMetric<T> implements MeasuringUnit<T> {

    protected MeasuringUnit<T> measuringUnit;
    protected Repository repository;

    protected Metric metric;
    protected String metricValue;

    protected AbstractMetric(MeasuringUnit<T> component, Metric metric, String defaultMetricValue) {
        this.measuringUnit = component;
        repository = new Repository(MeasuringUnit.project);
        this.metric = metric;
        this.metricValue = defaultMetricValue;
    }

    @Override
    public Map<Metric, String> getMetrics() {
        Map<Metric, String> m = measuringUnit.getMetrics();
        m.put(metric, getValueFromMetric(metric));
        return m;
    }

    @Override
    public String getValueFromMetric(Metric metric) {
        if(this.metric == metric){
            return metricValue;
        }else {
            return measuringUnit.getValueFromMetric(metric);
        }
    }

    @Override
    public void setMetricValue(Metric metric, String value) {
        if(metric == this.metric) metricValue = value;
        else measuringUnit.setMetricValue(metric, value);
    }

    @Override
    public MeasuringUnit<T> addMetrics(Metric... metrics) {
        return measuringUnit.addMetrics(metrics);
    }

    @Override
    public Release getRelease() {
        return measuringUnit.getRelease();
    }

    @Override
    public RepoFile getRepoClass() {
        return measuringUnit.getRepoClass();
    }

    @Override
    public Map<Metric, String> getMetrics(Metric... metrics) {
        return getMetrics(metrics);
    }

    public void calculateMetric(Metric metric, T input){
        if(metric == this.metric) calculateMetric(input);
        else measuringUnit.calculateMetric(metric, input);
    }

    public abstract void calculateMetric(T input);

}
