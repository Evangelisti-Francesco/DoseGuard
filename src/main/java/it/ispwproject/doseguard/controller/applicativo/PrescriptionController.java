package it.ispwproject.doseguard.controller.applicativo;

import it.ispwproject.doseguard.bean.PrescriptionBean;
import it.ispwproject.doseguard.dao.DAOFactory;
import it.ispwproject.doseguard.dao.PatientDAO;
import it.ispwproject.doseguard.dao.PrescriptionDAO;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Doctor;
import it.ispwproject.doseguard.model.Medication;
import it.ispwproject.doseguard.model.Patient;
import it.ispwproject.doseguard.model.Prescription;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;

import java.time.LocalDate;
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

        // 1. Recupera il Medico attualmente loggato
        if (!(SessionManager.getInstance().getLoggedUser() instanceof Doctor doctor)) {
            throw new DAOException("Operazione consentita solo ai medici autenticati.");
        }

        // 2. Recupera il Paziente tramite il suo Codice Fiscale
        Patient patient = patientDAO.findById(patientID);
        if (patient == null) {
            throw new DAOException("Paziente non trovato con il codice fiscale fornito: " + patientID);
        }

        // 3. Crea ed emette la ricetta (costruttore a 5 parametri)
        Prescription prescription = new Prescription(doctor, patient, drug, dosage, frequency,LocalDate.now());

        // 4. Salvataggio nel DAO
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

    // Metodo helper privato per la conversione Model -> Bean
    private PrescriptionBean mapModelToBean(Prescription p) {
        PrescriptionBean bean = new PrescriptionBean();
        bean.setId(p.getId());
        bean.setDoctorFullName(p.getDoctor() != null ? p.getDoctor().getFullName() : "");
        bean.setPatientFiscalCode(p.getPatient() != null ? p.getPatient().getFiscalCode() : "");
        bean.setPharmacistId(p.getPharmacistId());
        bean.setDrug(p.getDrug());
        bean.setDosage(p.getDosage());
        bean.setFrequency(p.getFrequency());
        bean.setIssueDate(p.getIssueDate());
        return bean;
    }
}
