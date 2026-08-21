package it.ispwproject.doseguard.bean;

import java.time.LocalDateTime;

public class PatientProgressBean {

    private PatientBean patient;
    private String notes;
    private LocalDateTime updatedAt;

    public PatientProgressBean() {}

    public PatientProgressBean(PatientBean patient, String notes) {
        this.patient = patient;
        this.notes = notes;
    }

    public PatientProgressBean(PatientBean patient, String notes, LocalDateTime updatedAt) {
        this.patient = patient;
        this.notes = notes;
        this.updatedAt = updatedAt;
    }

    public PatientBean getPatient() {
        return patient;
    }

    public void setPatient(PatientBean patient) {
        this.patient = patient;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}