package it.ispwproject.doseguard.bean;

import java.time.LocalDate;

public class PrescriptionBean {

    private int id;
    private String doctorFullName;
    private String patientFiscalCode;
    private Integer pharmacistId;
    private String drug;
    private String dosage;
    private String frequency;
    private LocalDate issueDate;

    public PrescriptionBean() {
    }


    public PrescriptionBean(int id, String doctorFullName, String patientFiscalCode,
                            Integer pharmacistId, String drug, String dosage,
                            String frequency, LocalDate issueDate) {
        this.id = id;
        this.doctorFullName = doctorFullName;
        this.patientFiscalCode = patientFiscalCode;
        this.pharmacistId = pharmacistId;
        this.drug = drug;
        this.dosage = dosage;
        this.frequency = frequency;
        this.issueDate = issueDate;
    }

    // Getter e Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDoctorFullName() {
        return doctorFullName;
    }

    public void setDoctorFullName(String doctorFullName) {
        this.doctorFullName = doctorFullName;
    }

    public String getPatientFiscalCode() {
        return patientFiscalCode;
    }

    public void setPatientFiscalCode(String patientFiscalCode) {
        this.patientFiscalCode = patientFiscalCode;
    }

    public Integer getPharmacistId() {
        return pharmacistId;
    }

    public void setPharmacistId(Integer pharmacistId) {
        this.pharmacistId = pharmacistId;
    }

    public String getDrug() {
        return drug;
    }

    public void setDrug(String drug) {
        this.drug = drug;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }
}
