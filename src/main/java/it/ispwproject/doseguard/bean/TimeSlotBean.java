package it.ispwproject.doseguard.bean;

import java.time.LocalDate;
import java.time.LocalTime;

public class TimeSlotBean {

    private int id;
    private LocalDate date;
    private LocalTime startTime;
    private String bookedByName;
    private boolean available;

    public TimeSlotBean() {}

    public TimeSlotBean(int id, LocalDate date, LocalTime startTime, boolean available) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.available = available;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public LocalDate getDate() {return date;}
    public void setDate(LocalDate date) {this.date = date;}

    public LocalTime getStartTime() {return startTime;}
    public void setStartTime(LocalTime startTime) {this.startTime = startTime;}

    public  String getBookedByName() {return bookedByName;}
    public void setBookedByName(String bookedByName) {this.bookedByName = bookedByName;}

    public boolean isAvailable() {return available;}
    public void setAvailable(boolean available) {this.available = available;}



}
