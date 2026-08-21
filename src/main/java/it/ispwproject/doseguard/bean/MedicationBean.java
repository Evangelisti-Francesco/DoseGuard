package it.ispwproject.doseguard.bean;

import java.time.LocalTime;

public class MedicationBean {

    private int id;
    private String name;
    private String dosage;
    private LocalTime scheduleTime;
    private boolean taken;

    public MedicationBean() {}

    public MedicationBean(int id, String name, String dosage, LocalTime scheduleTime, boolean taken) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.scheduleTime = scheduleTime;
        this.taken = taken;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public LocalTime getScheduleTime() { return scheduleTime; }
    public void setScheduleTime(LocalTime scheduleTime) { this.scheduleTime = scheduleTime; }

    public boolean isTaken() { return taken; }
    public void setTaken(boolean taken) { this.taken = taken; }
}
