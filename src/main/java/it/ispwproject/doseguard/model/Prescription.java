package it.ispwproject.doseguard.model;

import java.time.LocalDate;

public class Prescription {

    private int id;
    private Doctor doctor;
    private Patient patient;
    private Integer pharmacistId;
    private String drug;
    private String dosage;
    private String frequency;
    private LocalDate issueDate;


    public Prescription(Doctor doctor, Patient patient, String drug, String dosage, String frequency) {
        this.doctor = doctor;
        this.patient = patient;
        this.drug = drug;
        this.dosage = dosage;
        this.frequency = frequency;
        this.issueDate = LocalDate.now();
        this.pharmacistId = null;
    }
    // Getter e Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Integer getPharmacistId() { return pharmacistId; }
    public void setPharmacistId(Integer pharmacistId) { this.pharmacistId = pharmacistId; }

    public String getDrug() { return drug; }
    public void setDrug(String drug) { this.drug = drug; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
}