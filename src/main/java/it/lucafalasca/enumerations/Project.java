package it.lucafalasca.enumerations;

import java.time.LocalDate;
import java.time.Month;

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
                return LocalDate.of(2017, Month.NOVEMBER, 10);
            case AVRO:
                return LocalDate.now();
            default:
                return null;
        }
    }
}
