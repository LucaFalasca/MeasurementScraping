package it.lucafalasca.measurement.metrics;

import it.lucafalasca.entities.ClassContent;
import it.lucafalasca.entities.ModFile;
import it.lucafalasca.enumerations.Metric;
import it.lucafalasca.measurement.MeasuringUnit;
import it.lucafalasca.util.Count;
import it.lucafalasca.util.Decode;

public class ImportMetric extends AbstractMetric<ClassContent>{

    public ImportMetric(MeasuringUnit component) {
        super(component, Metric.IMPORT, "0");
    }

    @Override
    public void calculateMetric(ClassContent input) {
        String content = Decode.decodeBase64(input.getContent());
        metricValue = String.valueOf(Count.contaOccorrenze(content, "import"));
    }
}
