package it.ispwproject.doseguard.dao;

import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Doctor;
import it.ispwproject.doseguard.model.Specialization;

import java.util.List;

public interface DoctorDAO {

    List<Doctor> getBySpecialization(Specialization specialization) throws DAOException;
    List<Doctor> getAllDoctors() throws DAOException;
    Doctor findById(int id) throws DAOException;
}
