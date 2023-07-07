package it.lucafalasca.entities;
public class Ticket {

    private String key;
    private Fields fields;



    public void setKey(String key) {
        this.key = key;
    }

    public void setFields(Fields fields) {
        this.fields = fields;
    }


    public Ticket(String key, Fields fields) {
        this.key = key;
        this.fields = fields;
    }

    public String getKey() {
        return key;
    }

    public Fields getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "key='" + key + '\'' +
                ", fields=" + fields +
                '}';
    }
}


