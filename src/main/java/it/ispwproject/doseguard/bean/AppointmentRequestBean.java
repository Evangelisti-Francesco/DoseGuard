package it.ispwproject.doseguard.bean;

public class AppointmentRequestBean {

    private PatientBean patient;
    private DoctorBean doctor;
    private SpecializationBean specialization;
    private TimeSlotBean slot;
    private String notes;

    public AppointmentRequestBean() {}

    public AppointmentRequestBean(PatientBean patient, DoctorBean doctor, SpecializationBean specialization,TimeSlotBean slot, String notes) {
        this.patient = patient;
        this.doctor = doctor;
        this.specialization = specialization;
        this.slot = slot;
        this.notes = notes;
    }

    public PatientBean getPatient() {return patient;}
    public void setPatient(PatientBean patient) {this.patient = patient;}

    public DoctorBean getDoctor() {return doctor;}
    public void setDoctor(DoctorBean doctor) {this.doctor = doctor;}

    public SpecializationBean getSpecialization() {return specialization;}
    public void setSpecialization(SpecializationBean specialization) {this.specialization = specialization;}

    public TimeSlotBean getSlot() {return slot;}
    public void setSlot(TimeSlotBean slot) {this.slot = slot;}

    public String getNotes() {return notes;}
    public void setNotes(String notes) {this.notes = notes;}

}
