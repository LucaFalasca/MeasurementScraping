package it.lucafalasca.measurement.metrics;

import it.lucafalasca.entities.ModFile;
import it.lucafalasca.enumerations.Metric;
import it.lucafalasca.measurement.MeasuringUnit;

public class ChangesMetric extends AbstractMetric<ModFile>{

    public ChangesMetric(MeasuringUnit<ModFile> component) {
        super(component, Metric.CHANGES, "0");
    }

    @Override
    public void calculateMetric(ModFile input) {
        metricValue = String.valueOf(Integer.parseInt(metricValue) + input.getChanges());
    }
}
