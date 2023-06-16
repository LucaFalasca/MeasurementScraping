package it.lucafalasca.measurement.metrics;

import it.lucafalasca.entities.ModFile;
import it.lucafalasca.enumerations.Metric;
import it.lucafalasca.measurement.MeasuringUnit;

import java.util.Map;

public class LocMetric extends AbstractMetric<ModFile> {

    public LocMetric(MeasuringUnit component) {
        super(component, Metric.LOC, "0");
    }


    @Override
    public void calculateMetric(ModFile input) {
        int currentLoc = input.getAdditions() - input.getDeletions();
        metricValue = String.valueOf(Integer.parseInt(metricValue) + currentLoc);
    }
}
