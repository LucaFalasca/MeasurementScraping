package it.lucafalasca.measurement.metrics;

import it.lucafalasca.enumerations.Metric;
import it.lucafalasca.measurement.MeasuringUnit;

public class BuggynessMetric extends AbstractMetric<Boolean>{

    public BuggynessMetric(MeasuringUnit<Boolean> component) {
        super(component, Metric.BUGGYNESS, "0");
    }

    @Override
    public void calculateMetric(Boolean input) {
        if(Boolean.TRUE.equals(input))
            metricValue = "1";
        else
            metricValue = "0";
    }


}
