package it.ispwproject.doseguard.model;


import it.ispwproject.doseguard.enumerator.Role;

public class Doctor extends User {


    private String specialization;

    public Doctor() {}

    public Doctor(int id,String name, String surname, String email, String password,String specialization) {
        super(id,name,surname,email,password, Role.DOCTOR);
        this.specialization = specialization;
    }

    public String getSpecialization() {return specialization;}
    public void setSpecialization(String specialization) {this.specialization = specialization;}



}
