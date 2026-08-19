package it.ispwproject.doseguard.dao;

import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.User;

public interface RegistrationDAO {

    boolean emailExists(String email) throws DAOException;
    boolean fiscalCodeExists(String fiscalCode) throws DAOException;

    void saveUser(User user) throws DAOException;
}
