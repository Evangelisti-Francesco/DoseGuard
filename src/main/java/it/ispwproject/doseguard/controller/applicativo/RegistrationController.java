package it.ispwproject.doseguard.controller.applicativo;

import it.ispwproject.doseguard.bean.RegistrationBean;
import it.ispwproject.doseguard.bean.SpecializationBean;
import it.ispwproject.doseguard.dao.DAOFactory;
import it.ispwproject.doseguard.dao.RegistrationDAO;
import it.ispwproject.doseguard.dao.SpecializationDAO;
import it.ispwproject.doseguard.enumerator.Role;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.exception.LoginException;
import it.ispwproject.doseguard.exception.RegistrationException;
import it.ispwproject.doseguard.model.Doctor;
import it.ispwproject.doseguard.model.Patient;
import it.ispwproject.doseguard.model.Specialization;
import it.ispwproject.doseguard.model.User;
import it.ispwproject.doseguard.util.PasswordUtils;
import it.ispwproject.doseguard.util.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

public class RegistrationController {

    private final RegistrationDAO registrationDAO;
    private final SpecializationDAO specializationDAO;

    public RegistrationController() {
        this.registrationDAO = DAOFactory.getRegistrationDAO();
        this.specializationDAO = DAOFactory.getSpecializationDAO();
    }

    public List<SpecializationBean> getAvailableSpecializations() throws DAOException {
        List<SpecializationBean> result = new ArrayList<>();
        for (Specialization spec : specializationDAO.getAllSpecializations()) {
            result.add(new SpecializationBean(spec.getId(), spec.getSpecialization()));
        }
        return result;
    }

    public void register(RegistrationBean bean)
            throws DAOException, RegistrationException {

        validateBean(bean);

        if (registrationDAO.emailExists(bean.getEmail())) {
            throw new RegistrationException("Email già registrata. Usa un'altra email.");
        }

        if (bean.getRole() == Role.PATIENT
                && bean.getFiscalCode() != null
                && registrationDAO.fiscalCodeExists(bean.getFiscalCode())) {
            throw new RegistrationException("Codice Fiscale già presente nei nostri sistemi.");
        }

        String hashedPassword;
        try {
            hashedPassword = PasswordUtils.hash(bean.getPassword());
        } catch (LoginException e) {
            throw new RegistrationException("Errore durante la codifica della password.", e);
        }

        String specName = "";
        if (bean.getSpecializations() != null && !bean.getSpecializations().isEmpty()) {
            specName = bean.getSpecializations().get(0).getName(); // Prende il nome della specializzazione
        }

        User user;
        if (bean.getRole() == Role.DOCTOR) {
            user = new Doctor(0, bean.getName(), bean.getSurname(),
                    bean.getEmail(), hashedPassword, specName);
        } else {
            user = new Patient(0, bean.getName(), bean.getSurname(),
                    bean.getEmail(), hashedPassword, bean.getFiscalCode());
        }

        registrationDAO.saveUser(user);
    }

    private void validateBean(RegistrationBean bean) throws RegistrationException {
        if (bean == null) {
            throw new RegistrationException("Dati di registrazione non validi.");
        }
        validateRequiredField(bean.getName(), "Il nome è obbligatorio.");
        validateRequiredField(bean.getSurname(), "Il cognome è obbligatorio.");
        validateRequiredField(bean.getEmail(), "L'email è obbligatoria.");
        validateEmail(bean.getEmail());
        validatePassword(bean);
        validateRole(bean);
        validateDoctorFields(bean);
        validatePatientFields(bean);
    }

    private void validateRequiredField(String value, String message)
            throws RegistrationException {
        if (value == null || value.isBlank()) {
            throw new RegistrationException(message);
        }
    }

    private void validateEmail(String email) throws RegistrationException {
        if (!ValidationUtils.isValidEmail(email)) {
            throw new RegistrationException("Email non valida.");
        }
    }

    private void validatePassword(RegistrationBean bean) throws RegistrationException {
        if (bean.getPassword() == null || bean.getPassword().length() < 8) {
            throw new RegistrationException("La password deve essere di almeno 8 caratteri.");
        }
        if (bean.getPassword().chars().noneMatch(Character::isUpperCase)) {
            throw new RegistrationException("La password deve contenere almeno una lettera maiuscola.");
        }

        if (bean.getPassword().chars().noneMatch(Character::isDigit)) {
            throw new RegistrationException("La password deve contenere almeno un numero.");
        }
        if (!bean.getPassword().equals(bean.getConfirmPassword())) {
            throw new RegistrationException("Le password non coincidono.");
        }
    }

    private void validateRole(RegistrationBean bean) throws RegistrationException {
        if (bean.getRole() == null) {
            throw new RegistrationException("Seleziona un ruolo.");
        }
    }

    private void validateDoctorFields(RegistrationBean bean) throws RegistrationException {
        if (bean.getRole() != Role.DOCTOR) return;
        if (bean.getSpecializations() == null || bean.getSpecializations().isEmpty()) {
            throw new RegistrationException("Seleziona almeno una specializzazione.");
        }
    }

    private void validatePatientFields(RegistrationBean bean) throws RegistrationException {
        if (bean.getRole() != Role.PATIENT) return;
        validateRequiredField(bean.getFiscalCode(),"Il codice fiscale è obbligatorio.");
    }

}