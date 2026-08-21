package it.ispwproject.doseguard.controller.applicativo;

import it.ispwproject.doseguard.bean.PrescriptionBean;
import it.ispwproject.doseguard.dao.DAOFactory;
import it.ispwproject.doseguard.dao.PatientDAO;
import it.ispwproject.doseguard.dao.PrescriptionDAO;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Doctor;
import it.ispwproject.doseguard.model.Patient;
import it.ispwproject.doseguard.model.Prescription;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionController {

    private final PrescriptionDAO prescriptionDAO;
    private final PatientDAO patientDAO;

    public PrescriptionController() {
        this.prescriptionDAO = DAOFactory.getPrescriptionDAO();
        this.patientDAO = DAOFactory.getPatientDAO();
    }

    public boolean createPrescription(int patientID, String drug, String dosage, String frequency)
            throws DAOException {

        if (!(SessionManager.getInstance().getLoggedUser() instanceof Doctor doctor)) {
            throw new DAOException("Operazione consentita solo ai medici autenticati.");
        }

        Patient patient = patientDAO.findById(patientID);
        if (patient == null) {
            throw new DAOException("Paziente non trovato con l'ID fornito: " + patientID);
        }

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        Prescription prescription = new Prescription(doctor, patient, drug, dosage, frequency,today);


        prescriptionDAO.save(prescription);

        return true;
    }


    public List<PrescriptionBean> getPatientPrescriptions(String fiscalCode) throws DAOException {
        List<Prescription> prescriptions = this.prescriptionDAO.getByPatientFiscalCode(fiscalCode);
        List<PrescriptionBean> beans = new ArrayList<>();

        for (Prescription p : prescriptions) {
            beans.add(mapModelToBean(p));
        }

        return beans;
    }

    public PrescriptionBean getPrescriptionById(int prescriptionId) throws DAOException {
        Prescription p = this.prescriptionDAO.findById(prescriptionId);
        return (p != null) ? mapModelToBean(p) : null;
    }

    public void fulfillPrescription(int prescriptionId, int pharmacistId) throws DAOException {
        this.prescriptionDAO.markAsFulfilled(prescriptionId, pharmacistId);
    }

    private PrescriptionBean mapModelToBean(Prescription p) {
        PrescriptionBean bean = new PrescriptionBean();
        bean.setId(p.getId());
        bean.setDoctorFullName(p.getDoctor() != null ? p.getDoctor().getFullName() : "");
        bean.setPatientFiscalCode(p.getPatient() != null ? p.getPatient().getFiscalCode() : "");
        bean.setDrug(p.getDrug());
        bean.setDosage(p.getDosage());
        bean.setFrequency(p.getFrequency());
        bean.setIssueDate(p.getIssueDate());
        return bean;
    }
}
