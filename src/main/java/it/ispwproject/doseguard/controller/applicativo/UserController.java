package it.ispwproject.doseguard.controller.applicativo;

import it.ispwproject.doseguard.dao.DAOFactory;
import it.ispwproject.doseguard.dao.UserDAO;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import it.ispwproject.doseguard.util.ValidationUtils;

public class UserController {

    private final UserDAO userDAO;

    public UserController() {
        this.userDAO = DAOFactory.getUserDAO();
    }

    public void updateEmail(String newEmail) throws DAOException {
        if (newEmail == null || newEmail.isBlank()) {
            throw new DAOException("L'email non può essere vuota.");
        }
        if (!ValidationUtils.isValidEmail(newEmail)) {
            throw new DAOException("Formato email non valido.");
        }

        int id = SessionManager.getInstance().getLoggedUser().getId();
        userDAO.updateEmail(id, newEmail);
        SessionManager.getInstance().getLoggedUser().setEmail(newEmail);
    }
}