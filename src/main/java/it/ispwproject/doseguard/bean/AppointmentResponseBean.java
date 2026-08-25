package it.ispwproject.doseguard.bean;

import it.ispwproject.doseguard.enumerator.AppointmentStatus;

public class AppointmentResponseBean {

    private int id;
    private PatientBean patient;
    private DoctorBean doctor;
    private SpecializationBean specialization;
    private TimeSlotBean slot;
    private AppointmentStatus status;

    private String notes;

    public AppointmentResponseBean() {}

    public AppointmentResponseBean(int id, PatientBean patient, DoctorBean doctor, SpecializationBean specialization, TimeSlotBean slot, AppointmentStatus status,String notes) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.specialization = specialization;
        this.slot = slot;
        this.status = status;
        this.notes = notes;
    }


    public int getId() {return id;} public void setId(int id) {this.id = id;}

    public PatientBean getPatient() {return patient;} public void setPatient(PatientBean patient) {this.patient = patient;}

    public DoctorBean getDoctor() {return doctor;} public void setDoctor(DoctorBean doctor) {this.doctor = doctor;}

    public SpecializationBean getSpecialization() {return specialization;} public void setSpecialization(SpecializationBean specialization) {this.specialization = specialization;}

    public TimeSlotBean getSlot() {return slot;} public void setSlot(TimeSlotBean slot) {this.slot = slot;}

    public AppointmentStatus getStatus() {return status;} public void setStatus(AppointmentStatus status) {this.status = status;}

    public String getNotes() {return notes;} public void setNotes(String notes) {this.notes = notes;}

}
