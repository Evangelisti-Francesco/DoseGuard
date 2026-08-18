package it.ispwproject.doseguard.dao;

import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Specialization;

import java.util.List;

public interface SpecializationDAO {

    List<Specialization> getAllSpecializations() throws DAOException;

    Specialization findById(int id) throws DAOException;
}
