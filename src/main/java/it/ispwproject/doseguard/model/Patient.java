package it.ispwproject.doseguard.model;

import it.ispwproject.doseguard.enumerator.Role;

import java.util.ArrayList;
import java.util.List;

public class Patient extends User {

    private List<Doctor> favouriteDoctors;

    //Costruttori
    public Patient() {
        super();
        this.favouriteDoctors = new ArrayList<Doctor>();
    }

    public Patient(int id, String name, String surname, String email, String password, String role) {
        super(id,name,surname,email,password, Role.PATIENT);
        this.favouriteDoctors = new ArrayList<>();
    }

    public boolean hasFavourite(int doctorId) {
        return favouriteDoctors.stream().anyMatch(d -> d.getId() == doctorId );
    }

    public void addFavouriteDoctor(Doctor doctor) {
        if(!hasFavourite(doctor.getId())){
            favouriteDoctors.add(doctor);
        }
    }

    public void removeFavouriteDoctor(int doctorId) {
        favouriteDoctors.removeIf(d -> d.getId() == doctorId);
    }

    public List<Doctor> getFavouriteDoctors() { return favouriteDoctors;}
    public void setFavouriteDoctors(List<Doctor> favouriteDoctors) {this.favouriteDoctors = favouriteDoctors;}

}
