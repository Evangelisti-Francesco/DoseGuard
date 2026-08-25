package it.ispwproject.doseguard.model;

import java.time.LocalDate;

public class Prescription {

    private int id;
    private Doctor doctor;
    private Patient patient;
    private String drug;
    private String dosage;
    private String frequency;
    private LocalDate issueDate;


    public Prescription(Doctor doctor, Patient patient, String drug, String dosage, String frequency,LocalDate issueDate) {
        this.doctor = doctor;
        this.patient = patient;
        this.drug = drug;
        this.dosage = dosage;
        this.frequency = frequency;
        this.issueDate = issueDate;
    }
    // Getter e Setter
    public int getId() { return id; } public void setId(int id) { this.id = id; }

    public Doctor getDoctor() { return doctor; } public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public Patient getPatient() { return patient; } public void setPatient(Patient patient) { this.patient = patient; }

    public String getDrug() { return drug; } public void setDrug(String drug) { this.drug = drug; }

    public String getDosage() { return dosage; } public void setDosage(String dosage) { this.dosage = dosage; }

    public String getFrequency() { return frequency; } public void setFrequency(String frequency) { this.frequency = frequency; }

    public LocalDate getIssueDate() { return issueDate; } public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
}