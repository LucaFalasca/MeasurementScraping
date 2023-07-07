package it.lucafalasca.enumerations;

import java.time.LocalDate;

public enum Project {
    BOOKKEEPER("BOOKKEEPER"),
    AVRO("AVRO");

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
                return null;
        }
    }

    public static Project getProjectByName(String name){
        switch (name){
            case "BOOKKEEPER":
                return BOOKKEEPER;
            case "AVRO":
                return AVRO;
            default:
                return null;
        }
    }
}
