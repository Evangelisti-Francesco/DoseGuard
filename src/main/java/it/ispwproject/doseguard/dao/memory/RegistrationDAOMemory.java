package it.ispwproject.doseguard.dao.memory;

import it.ispwproject.doseguard.dao.RegistrationDAO;
import it.ispwproject.doseguard.demo.DemoDataStore;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Patient;
import it.ispwproject.doseguard.model.User;

public class RegistrationDAOMemory implements RegistrationDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public boolean emailExists(String email) throws DAOException {
        return store.getUsers().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public boolean fiscalCodeExists(String fiscalCode) throws DAOException {
        if (fiscalCode == null) return false;
        return store.getUsers().stream()
                .filter(Patient.class::isInstance)
                .map(Patient.class::cast)
                .anyMatch(p -> p.getFiscalCode().equalsIgnoreCase(fiscalCode));
    }

    @Override
    public void saveUser(User user) throws DAOException {
        user.setId(store.nextUserId());
        store.getUsers().add(user);
    }

}
