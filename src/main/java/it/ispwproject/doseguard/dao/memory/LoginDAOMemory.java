package it.ispwproject.doseguard.dao.memory;

import it.ispwproject.doseguard.dao.LoginDAO;
import it.ispwproject.doseguard.demo.DemoDataStore;
import it.ispwproject.doseguard.exception.LoginException;
import it.ispwproject.doseguard.model.Credentials;
import it.ispwproject.doseguard.model.User;

public class LoginDAOMemory implements LoginDAO {

    @Override
    public Credentials execute(String email, String plainPassword) throws LoginException {
        DemoDataStore store = DemoDataStore.getInstance();

        User user = store.getUsers().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new LoginException("Credenziali non valide. Riprova."));

        if (plainPassword == null || plainPassword.isBlank()) {
            throw new LoginException("Credenziali non valide. Riprova.");
        }

        return new Credentials(email, plainPassword, user.getRole());
    }


}
