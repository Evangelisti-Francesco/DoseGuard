package it.ispwproject.doseguard.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class TimeSlot {

    private int id;
    private Patient patient;

    private LocalDate date;
    private LocalTime startTime;
    private boolean available;

    // COSTRUTTORI
    public TimeSlot() {}

    public TimeSlot(int id, LocalDate date, LocalTime startTime, boolean available) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.available = available;
    }

    public TimeSlot(int id, Patient patient, LocalDate date, LocalTime startTime, boolean available) {
        this.id = id;
        this.patient = patient;
        this.date = date;
        this.startTime = startTime;
        this.available = available;
    }

    public void reserve(){
        this.available = false;
    }

    public void release(){
        this.available = true;
        this.patient = null;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }


}
