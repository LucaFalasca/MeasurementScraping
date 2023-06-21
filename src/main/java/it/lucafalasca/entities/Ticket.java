package it.lucafalasca.entities;
public class Ticket {

    private String key;
    private Fields fields;

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
        return "Ticket{\n" +
                "key='" + key + '\'' +
                ", fields=" + fields +
                '}';
    }
}


