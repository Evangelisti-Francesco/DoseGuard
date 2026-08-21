package it.ispwproject.doseguard.dao;

import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.PatientProgress;

public interface ProgressDAO {
    void saveOrUpdate(PatientProgress progress) throws DAOException;
    PatientProgress findByPatientAndDoctor(int doctorId, int patientId) throws DAOException;
}
