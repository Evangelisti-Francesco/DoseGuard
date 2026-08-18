package it.ispwproject.doseguard.model;

import it.ispwproject.doseguard.enumerator.AppointmentStatus;
import it.ispwproject.doseguard.pattern.observer.Observable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public class Booking extends Observable {

    private int id;

    private Patient patient;
    private Doctor doctor;
    private Specialization specialization;
    private TimeSlot timeSlot;

    private AppointmentStatus status;
    private String notes;
    private LocalDateTime creationAt;

    public Booking(){}

    public Booking(Patient patient, Doctor doctor, Specialization specialization, TimeSlot timeSlot,String notes){
        this.patient = patient;
        this.doctor = doctor;
        this.specialization = specialization;
        this.timeSlot = timeSlot;
        this.status = AppointmentStatus.PENDING;
        this.notes = notes;
        this.creationAt = LocalDateTime.now(ZoneId.systemDefault());
    }

    public void confirm(){
        this.status = AppointmentStatus.CONFIRMED;
        notifyObservers();
    }

    public void cancel(){
        this.status = AppointmentStatus.CANCELLED;
        notifyObservers();
    }

    public boolean isPending(){
        return this.status == AppointmentStatus.PENDING;
    }

    // GETTER e SETTER

    public int getId() {return this.id;}
    public void setId(int id) {this.id = id;}

    public Patient getPatient() {return this.patient;}
    public void setPatient(Patient patient) {this.patient = patient;}

    public Doctor getDoctor() {return this.doctor;}
    public void setDoctor(Doctor doctor) {this.doctor = doctor;}

    public Specialization getSpecialization() {return this.specialization;}
    public void setSpecialization(Specialization specialization) {this.specialization = specialization;}

    public TimeSlot getTimeSlot() {return this.timeSlot;}
    public void setTimeSlot(TimeSlot timeSlot) {this.timeSlot = timeSlot;}

    public AppointmentStatus getStatus() {return this.status;}
    public void setStatus(AppointmentStatus status) {this.status = status;}

    public String getNotes() {return this.notes;}
    public void setNotes(String notes) {this.notes = notes;}

    public LocalDateTime getCreationAt() {return this.creationAt;}
    public void setCreationAt(LocalDateTime creationAt) {this.creationAt = creationAt;}

}
