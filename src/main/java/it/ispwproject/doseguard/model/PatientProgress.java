package it.ispwproject.doseguard.model;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class PatientProgress {

    private int id;

    private Doctor doctor;
    private Patient patient;

    private String notes;
    private LocalDateTime updatedAt;

    public PatientProgress() {}

    public PatientProgress(Doctor doctor, Patient patient, String notes) {
        this.doctor  = doctor;
        this.patient = patient;
        this.notes   = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public String getNotes() { return notes; }
    public void updateNotes(String newNotes) {
        this.notes = newNotes;
        this.updatedAt = LocalDateTime.now(ZoneId.systemDefault());
    }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
