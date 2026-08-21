package it.ispwproject.doseguard.controller.applicativo;

import it.ispwproject.doseguard.bean.MedicationBean;
import it.ispwproject.doseguard.dao.DAOFactory;
import it.ispwproject.doseguard.dao.MedicationDAO;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Medication;
import it.ispwproject.doseguard.model.Patient;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MedicationController {

    private final MedicationDAO medicationDAO;

    public MedicationController() {
        this.medicationDAO = DAOFactory.getMedicationDAO();
    }

    public List<MedicationBean> getPatientMedications() throws DAOException {
        if (!(SessionManager.getInstance().getLoggedUser() instanceof Patient patient)) {
            throw new DAOException("Operazione consentita solo ai pazienti autenticati.");
        }

        List<Medication> medications = medicationDAO.getByPatient(patient.getId());
        List<MedicationBean> beans = new ArrayList<>();

        for (Medication m : medications) {
            beans.add(mapModelToBean(m));
        }

        return beans;
    }

    public void markMedicationAsTaken(int medicationId) throws DAOException {
        if (!(SessionManager.getInstance().getLoggedUser() instanceof Patient patient)) {
            throw new DAOException("Operazione consentita solo ai pazienti autenticati.");
        }

        medicationDAO.markAsTaken(medicationId, patient.getId());
    }

    private MedicationBean mapModelToBean(Medication m) {
        MedicationBean bean = new MedicationBean();
        bean.setId(m.getId());
        bean.setName(m.getName());
        bean.setDosage(m.getDosage());
        bean.setScheduleTime(m.getScheduleTime());
        bean.setTaken(m.isTaken());
        return bean;
    }
}
