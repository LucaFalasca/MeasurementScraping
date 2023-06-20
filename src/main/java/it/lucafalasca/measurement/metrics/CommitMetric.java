package it.lucafalasca.measurement.metrics;

import it.lucafalasca.enumerations.Metric;
import it.lucafalasca.measurement.MeasuringUnit;

public class CommitMetric extends AbstractMetric<Integer>{

    public CommitMetric(MeasuringUnit measuringUnit){
        super(measuringUnit, Metric.COMMIT, "0");
    }

    @Override
    public void calculateMetric(Integer input) {
        metricValue = String.valueOf(Integer.parseInt(metricValue) + input);
    }

}
