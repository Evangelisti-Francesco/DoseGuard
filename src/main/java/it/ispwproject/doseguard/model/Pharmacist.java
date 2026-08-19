package it.ispwproject.doseguard.model;

import it.ispwproject.doseguard.enumerator.Role;

public class Pharmacist extends User{

    private String pharmacyName;

    public Pharmacist(){}

    public Pharmacist(int id, String name, String surname, String email, String password, String pharmacyName) {
        super(id, name, surname, email, password, Role.PHARMACIST);
        this.pharmacyName = pharmacyName;
    }

    public String getPharmacyName() { return pharmacyName; }
    public void setPharmacyName(String pharmacyName) { this.pharmacyName = pharmacyName; }

}
