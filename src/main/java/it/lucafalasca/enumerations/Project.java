package it.lucafalasca.enumerations;

import java.time.LocalDate;

public enum Project {
    BOOKKEEPER("BOOKKEEPER"),
    AVRO("AVRO"),
    ACCUMULO("ACCUMULO"),
    TAJO("TAJO"),
    PIG("PIG"),
    SYNCOPE("SYNCOPE"),
    KAFKA("KAFKA"),
    ZOOKEEPER("ZOOKEEPER"),
    AGE("AGE"),
    CASSANDRA("CASSANDRA"),
    FLINK("FLINK"),
    HADOOP("HADOOP"),
    HBASE("HBASE"),
    HIVE("HIVE"),
    KUDU("KUDU"),
    KYLIN("KYLIN"),
    LUCENE("LUCENE"),
    MAHOUT("MAHOUT"),
    NUTCH("NUTCH"),
    OPENJPA("OPENJPA"),
    PARQUET("PARQUET"),
    PHOENIX("PHOENIX"),
    SENTRY("SENTRY");

    private final String name;
    Project(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }

    public LocalDate getFinalDate(){
        switch (this){
            case BOOKKEEPER:
                return LocalDate.of(2017, 6, 16);
            case AVRO:
                return LocalDate.now();
            default:
                return LocalDate.now();
        }
    }

    public static Project getProjectByName(String name){
        switch (name){
            case "BOOKKEEPER":
                return BOOKKEEPER;
            case "AVRO":
                return AVRO;
            case "ACCUMULO":
                return ACCUMULO;
            case "TAJO":
                return TAJO;
            case "PIG":
                return PIG;
            case "SYNCOPE":
                return SYNCOPE;
            case "KAFKA":
                return KAFKA;
            case "ZOOKEEPER":
                return ZOOKEEPER;
            case "AGE":
                return AGE;
            case "CASSANDRA":
                return CASSANDRA;
            case "FLINK":
                return FLINK;
            case "HADOOP":
                return HADOOP;
            case "HBASE":
                return HBASE;
            case "HIVE":
                return HIVE;
            case "KUDU":
                return KUDU;
            case "KYLIN":
                return KYLIN;
            case "LUCENE":
                return LUCENE;
            case "MAHOUT":
                return MAHOUT;
            case "NUTCH":
                return NUTCH;
            case "OPENJPA":
                return OPENJPA;
            case "PARQUET":
                return PARQUET;
            case "PHOENIX":
                return PHOENIX;
            case "SENTRY":
                return SENTRY;
            default:
                return null;
        }
    }

    public static Project[] getAllProjects(){
        return new Project[]{ACCUMULO, TAJO, PIG, SYNCOPE, KAFKA, ZOOKEEPER, AGE, CASSANDRA, FLINK, HADOOP, HBASE, HIVE, KUDU, KYLIN, LUCENE, MAHOUT, NUTCH, OPENJPA, PARQUET, PHOENIX, SENTRY};
    }
}
