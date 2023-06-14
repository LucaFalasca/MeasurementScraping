package it.lucafalasca.measurement.metrics;

import it.lucafalasca.Repository;
import it.lucafalasca.entities.Release;
import it.lucafalasca.entities.RepoFile;
import it.lucafalasca.enumerations.Metric;
import it.lucafalasca.measurement.MeasuringUnit;

public abstract class AbstractMetric implements MeasuringUnit {

    protected MeasuringUnit measuringUnit;
    protected Repository repository;

    protected AbstractMetric(MeasuringUnit component) {
        this.measuringUnit = component;
        repository = new Repository(MeasuringUnit.project);
    }

    @Override
    public MeasuringUnit addMetrics(Metric... metrics) {
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

}
