package it.ispwproject.doseguard.dao.memory;

import it.ispwproject.doseguard.dao.UserDAO;
import it.ispwproject.doseguard.demo.DemoDataStore;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDAOMemory implements UserDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();
    @Override
    public User findByEmail(String email) throws DAOException {
        return store.getUsers().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new DAOException("Utente non trovato con email: " + email));
    }

    @Override
    public User findById(int id) throws DAOException {
        return store.getUsers().stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElseThrow(() -> new DAOException("Utente non trovato con ID: " + id));
    }

    @Override
    public void updateEmail(int id, String newEmail) throws DAOException {
        User user = findById(id);
        user.setEmail(newEmail);
    }

    @Override
    public List<User> getAll() throws DAOException {
        return new ArrayList<>(store.getUsers());
    }

}
