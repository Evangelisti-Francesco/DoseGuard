package it.ispwproject.doseguard.pattern.singleton;

import it.ispwproject.doseguard.bean.SessionBean;
import it.ispwproject.doseguard.enumerator.Role;
import it.ispwproject.doseguard.model.User;

public class SessionManager {

    private User loggedUser;
    private SessionBean sessionBean;

    private SessionManager() {}

    private static class Holder {
        private static final SessionManager INSTANCE = new SessionManager();
    }

    public static SessionManager getInstance() {
        return Holder.INSTANCE;
    }

    public void setLoggedUser(User user) {
        this.loggedUser = user;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public boolean isLoggedIn() {
        return loggedUser != null;
    }

    public boolean isPatient() {
        return isLoggedIn() && loggedUser.hasRole(Role.PATIENT);
    }

    public boolean isDoctor() {
        return isLoggedIn() && loggedUser.hasRole(Role.DOCTOR);
    }

    public boolean isPharmacist() {
        return isLoggedIn() && loggedUser.hasRole(Role.PHARMACIST);
    }

    public void clearSession() {
        this.loggedUser = null;
        this.sessionBean = null;
    }
}
