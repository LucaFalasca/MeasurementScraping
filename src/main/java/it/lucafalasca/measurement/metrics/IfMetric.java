package it.lucafalasca.measurement.metrics;

import it.lucafalasca.entities.ClassContent;
import it.lucafalasca.enumerations.Metric;
import it.lucafalasca.measurement.MeasuringUnit;
import it.lucafalasca.util.Decode;

import java.util.Map;

public class IfMetric extends AbstractMetric<ClassContent>{

    public IfMetric(MeasuringUnit component) {
        super(component, Metric.NUM_IF, "0");
    }

    @Override
    public void calculateMetric(ClassContent input) {
        String content = Decode.decodeBase64(input.getContent());
        metricValue = String.valueOf(contaOccorrenze(content, "if(") + contaOccorrenze(content, "if ("));
        if(input.getPath().equals("bookkeeper-server/src/main/java/org/apache/bookkeeper/proto/PerChannelBookieClient.java")){
            //System.out.println(content);
            //System.out.println("IF: " + metricValue);
        }
    }

    private int contaOccorrenze(String testo, String sottostringa) {
        int contatore = 0;
        int indice = testo.indexOf(sottostringa);
        while (indice != -1) {
            contatore++;
            indice = testo.indexOf(sottostringa, indice + 1);
        }
        return contatore;
    }
}
