package it.ispwproject.doseguard.dao;

import it.ispwproject.doseguard.exception.LoginException;
import it.ispwproject.doseguard.model.Credentials;

public interface LoginDAO {
    Credentials execute(String email, String password) throws LoginException;
}
