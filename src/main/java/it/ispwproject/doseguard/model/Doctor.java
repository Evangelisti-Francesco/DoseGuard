package it.ispwproject.doseguard.model;


import it.ispwproject.doseguard.enumerator.Role;

public class Doctor extends User {

    //private int id;
    private String specialization;

    public Doctor() {};

    public Doctor(int id,String name, String surname, String email, String password, String role) {
        super(id,name,surname,email,password, Role.PATIENT);
        this.specialization = specialization;
    }

    public String getSpecialization() {return specialization;}
    public void setSpecialization(String specialization) {this.specialization = specialization;}



}
