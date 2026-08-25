package it.ispwproject.doseguard.controller.applicativo;

import it.ispwproject.doseguard.dao.DAOFactory;
import it.ispwproject.doseguard.exception.LoginException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class LoginControllerTest {

    private LoginController loginController;

    @BeforeEach
    void setup() {
        DAOFactory.setPersistence(DAOFactory.MEMORY);
        loginController = new LoginController();
    }

    @Test
    void testLoginConCredenzialiErrate() {
        // Tenta il login con un'email non registrata
        // Deve lanciare LoginException
        assertThrows(LoginException.class, () ->
                loginController.login("nonregistrato@demo", "password123")
        );
    }
}
