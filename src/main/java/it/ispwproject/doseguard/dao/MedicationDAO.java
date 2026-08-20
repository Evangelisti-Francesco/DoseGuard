package it.ispwproject.doseguard.dao;

import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Medication;

import java.util.List;

public interface MedicationDAO {

    List<Medication> getByPatient(int patientId) throws DAOException;
    void save(Medication medication) throws DAOException;
    void markAsTaken(int medicationId, int patientId) throws DAOException;
    void delete(int medicationId, int patientId) throws DAOException;


}
