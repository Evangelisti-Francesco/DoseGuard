package it.ispwproject.doseguard.controller.applicativo;

import it.ispwproject.doseguard.bean.SessionBean;
import it.ispwproject.doseguard.dao.ConnectionFactory;
import it.ispwproject.doseguard.dao.DAOFactory;
import it.ispwproject.doseguard.dao.UserDAO;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.exception.LoginException;
import it.ispwproject.doseguard.model.Credentials;
import it.ispwproject.doseguard.model.User;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import it.ispwproject.doseguard.util.PasswordUtils;

import java.sql.SQLException;

public class LoginController {

    public enum LoginResult {
        SUCCESSO_PATIENT,
        SUCCESSO_DOCTOR,
        SUCCESSO_PHARMACIST
    }

    public LoginResult login(String email, String password) throws LoginException {

        String hashedPassword = PasswordUtils.hash(password);
        Credentials credentials = DAOFactory.getLoginDAO().execute(email, hashedPassword);

        if (!DAOFactory.MEMORY.equalsIgnoreCase(DAOFactory.getPersistence())) {
            try {
                ConnectionFactory.changeRole(credentials.getRole());
            } catch (SQLException e) {
                throw new LoginException("Errore durante il cambio ruolo: " + e.getMessage(), e);
            }
        }

        User user;
        try {
            UserDAO userDAO = DAOFactory.getUserDAO();
            user = userDAO.findByEmail(email);
        } catch (DAOException e) {
            throw new LoginException("Errore nel caricamento utente: " + e.getMessage(), e);
        }

        SessionManager.getInstance().setLoggedUser(user);
        SessionManager.getInstance().setSessionBean(
                new SessionBean(user.getEmail(), credentials.getRole())
        );

        return switch (credentials.getRole()) {
            case PATIENT -> LoginResult.SUCCESSO_PATIENT;
            case DOCTOR  -> LoginResult.SUCCESSO_DOCTOR;
            case PHARMACIST   -> LoginResult.SUCCESSO_PHARMACIST;
        };
    }
}