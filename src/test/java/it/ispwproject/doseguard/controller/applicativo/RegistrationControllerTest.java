package it.ispwproject.doseguard.controller.applicativo;

import it.ispwproject.doseguard.bean.RegistrationBean;
import it.ispwproject.doseguard.dao.DAOFactory;
import it.ispwproject.doseguard.demo.DemoDataStore;
import it.ispwproject.doseguard.enumerator.Role;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.exception.RegistrationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ------------------------------------------------------------
 * Test Class : RegistrationControllerTest
 * Description: Verifica che il sistema impedisca la registrazione
 *              di due account con la stessa email. Dopo una prima
 *              registrazione avvenuta con successo, un secondo
 *              tentativo con la stessa email deve lanciare una
 *              RegistrationException.
 * ------------------------------------------------------------
 */

class RegistrationControllerTest {

    private RegistrationController registrationController;

    @BeforeEach
    void setup() {
        DemoDataStore.reset();
        DAOFactory.setPersistence(DAOFactory.MEMORY);
        registrationController = new RegistrationController();
    }

    @Test
    void testRegistrazioneConEmailDuplicata() throws DAOException, RegistrationException {
        // Prima registrazione — deve andare a buon fine
        RegistrationBean bean = new RegistrationBean();
        bean.setName("Mario");
        bean.setSurname("Rossi");
        bean.setEmail("mario@test.com");
        bean.setPassword("Password123");
        bean.setConfirmPassword("Password123");
        bean.setRole(Role.PATIENT); // Adattato al ruolo del tuo dominio (o PATIENT / DOCTOR)
        bean.setFiscalCode("RSSMRA80A01H501U");

        registrationController.register(bean);

        // Seconda registrazione con la stessa email — deve lanciare RegistrationException
        RegistrationBean duplicato = new RegistrationBean();
        duplicato.setName("Mario");
        duplicato.setSurname("Rossi");
        duplicato.setEmail("mario@test.com");
        duplicato.setPassword("Password123");
        duplicato.setConfirmPassword("Password123");
        duplicato.setRole(Role.PATIENT);
        duplicato.setFiscalCode("RSSMRA80A01H501U");

        assertThrows(RegistrationException.class, () ->
                registrationController.register(duplicato)
        );
    }
}
