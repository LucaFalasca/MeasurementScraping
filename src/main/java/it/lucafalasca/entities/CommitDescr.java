package it.lucafalasca.entities;

public class CommitDescr {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    @Override
    public String toString() {
        return "CommitDescr{" +
                "message='" + message + '\'' +
                '}';
    }
}
