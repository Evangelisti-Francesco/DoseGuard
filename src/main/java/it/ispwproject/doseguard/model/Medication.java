package it.ispwproject.doseguard.model;

import java.time.LocalTime;

public class Medication {

    private int id;
    private Patient patient;
    private String name;
    private String dosage;
    private LocalTime scheduleTime;
    private boolean taken;


    public Medication(int id, Patient patient, String name, String dosage,
                      LocalTime scheduleTime, boolean taken) {
        this.id = id;
        this.patient = patient;
        this.name = name;
        this.dosage = dosage;
        this.scheduleTime = scheduleTime;
        this.taken = taken;
    }

    public void markAsTaken() {
        this.taken = true;
    }

    public void resetDailyStatus() {
        this.taken = false;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public LocalTime getScheduleTime() { return scheduleTime; }
    public void setScheduleTime(LocalTime scheduleTime) { this.scheduleTime = scheduleTime; }

    public boolean isTaken() { return taken; }
    public void setTakenToday(boolean taken) { this.taken = taken; }
}