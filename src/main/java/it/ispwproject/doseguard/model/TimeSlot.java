package it.ispwproject.doseguard.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeSlot {

    private int id;
    private Doctor doctor;

    private LocalDate date;
    private LocalTime startTime;
    private boolean available;
    private LocalDateTime reservedUntil;

    public TimeSlot() {}

    public TimeSlot(int id, LocalDate date, LocalTime startTime) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.available = true;
    }

    public TimeSlot(int id, Doctor doctor, LocalDate date, LocalTime startTime) {
        this.id = id;
        this.doctor = doctor;
        this.date = date;
        this.startTime = startTime;
        this.available = true;
    }

    public void reserve(){this.available = false;}
    public void release(){
        this.available = true;
        this.doctor = null;
    }

    public boolean overlaps(TimeSlot other) {
        if (other == null || this.date == null || this.startTime == null) {
            return false;
        }
        // C'è sovrapposizione se coincidono sia la data che l'ora di inizio
        return this.date.equals(other.getDate()) &&
                this.startTime.equals(other.getStartTime());
    }


    public LocalDateTime getReservedUntil() { return reservedUntil; }
    public void setReservedUntil(LocalDateTime reservedUntil) { this.reservedUntil = reservedUntil; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }


}
