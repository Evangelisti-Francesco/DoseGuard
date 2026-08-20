package it.ispwproject.doseguard.model;

import java.time.LocalTime;

public class Medication {

    private int id;
    private Patient patient;
    private String name;
    private String dosage;
    private LocalTime scheduleTime;
    private boolean takenToday;


    public Medication(int id, Patient patient, String name, String dosage,
                      LocalTime scheduleTime, boolean takenToday) {
        this.id = id;
        this.patient = patient;
        this.name = name;
        this.dosage = dosage;
        this.scheduleTime = scheduleTime;
        this.takenToday = takenToday;
    }

    public void markAsTaken() {
        this.takenToday = true;
    }

    public void resetDailyStatus() {
        this.takenToday = false;
    }

    // Getter e Setter
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

    public boolean isTakenToday() { return takenToday; }
    public void setTakenToday(boolean takenToday) { this.takenToday = takenToday; }
}