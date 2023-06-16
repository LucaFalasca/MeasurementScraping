package it.lucafalasca.measurement.metrics;

import it.lucafalasca.enumerations.Metric;
import it.lucafalasca.measurement.MeasuringUnit;

import java.util.Map;

public class IfMetric extends AbstractMetric{

    public IfMetric(MeasuringUnit component) {
        super(component, Metric.NUM_IF, "0");
    }

    @Override
    public void calculateMetric(Object input) {

    }
}
