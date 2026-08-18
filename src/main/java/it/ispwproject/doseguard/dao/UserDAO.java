package it.ispwproject.doseguard.dao;


import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.User;

import java.util.List;

public interface UserDAO {

    User findByEmail(String email) throws DAOException;
    User findById(int id) throws DAOException;
    void updateEmail(int id, String newEmail) throws DAOException;
    List<User> getAll() throws DAOException;
}
