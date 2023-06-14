package it.lucafalasca.measurement.metrics;

import it.lucafalasca.enumerations.Metric;
import it.lucafalasca.measurement.MeasuringUnit;

import java.util.Map;

public class LocMetric extends AbstractMetric {

    private String loc;

    public LocMetric(MeasuringUnit component) {
        super(component);
        loc = "0";
    }

    @Override
    public Map<Metric, String> getMetrics() {
        Map<Metric, String> m = measuringUnit.getMetrics();
        m.put(Metric.LOC, getValueFromMetric(Metric.LOC));
        return m;
    }


    @Override
    public Map<Metric, String> getMetrics(Metric... metrics) {
        return getMetrics(metrics);
    }

    @Override
    public String getValueFromMetric(Metric metric) {
        if(metric == Metric.LOC){
            return loc;
        }else {
            return measuringUnit.getValueFromMetric(metric);
        }
    }

    @Override
    public void setMetricValue(Metric metric, String value) {
        if(metric == Metric.LOC) loc = value;
        else measuringUnit.setMetricValue(metric, value);
    }


}
