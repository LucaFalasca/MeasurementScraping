package it.lucafalasca.measurement.metrics;

import it.lucafalasca.entities.ModFile;
import it.lucafalasca.enumerations.Metric;
import it.lucafalasca.measurement.MeasuringUnit;

public class ChurnMetric extends AbstractMetric<ModFile> {

    public ChurnMetric(MeasuringUnit<ModFile> component) {
        super(component, Metric.CHURN, "0");
    }


    @Override
    public void calculateMetric(ModFile input) {
        int currentChurn = input.getAdditions() - input.getDeletions();
        metricValue = String.valueOf(Integer.parseInt(metricValue) + currentChurn);
    }
}
