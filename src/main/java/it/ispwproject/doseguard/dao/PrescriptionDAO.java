package it.ispwproject.doseguard.dao;

import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Prescription;

import java.util.List;

public interface PrescriptionDAO {

    void save(Prescription prescription) throws DAOException;
    List<Prescription> getByPatientFiscalCode(String fiscalCode) throws DAOException;
    Prescription findById(int prescriptionId) throws DAOException;
    List<Prescription> findByPatientId(int patientId) throws DAOException;
    void markAsFulfilled(int prescriptionId, int pharmacistId) throws DAOException;

}
