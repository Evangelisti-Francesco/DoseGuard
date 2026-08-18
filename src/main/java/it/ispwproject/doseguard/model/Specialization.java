package it.ispwproject.doseguard.model;

public class Specialization {

    private int id;
    private String name;

    public  Specialization() {}

    public Specialization(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getSpecialization() {return name;}
    public void setSpecialization(String name) {this.name = name;}

}
