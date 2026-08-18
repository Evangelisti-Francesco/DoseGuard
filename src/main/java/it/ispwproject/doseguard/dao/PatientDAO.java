package it.ispwproject.doseguard.dao;

import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Patient;

import java.util.List;

public interface PatientDAO {

    //Trova un paziente specifico tramite l'id
    Patient findById(int id) throws DAOException;
    List<Patient> getByDoctor(int doctorId) throws DAOException;
    void addFavouriteDoctor(int patientId, int doctorId) throws DAOException;
    void removeFavouriteDoctor(int patientId, int doctorId) throws DAOException;
    boolean isFavouriteDoctor(int patientId, int doctorId) throws DAOException;

}
