package it.ispwproject.doseguard.controller.applicativo;

import it.ispwproject.doseguard.bean.TimeSlotBean;
import it.ispwproject.doseguard.dao.DAOFactory;
import it.ispwproject.doseguard.demo.DemoDataStore;
import it.ispwproject.doseguard.exception.AvailabilityException;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Doctor;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ------------------------------------------------------------
 * Test Class : AvailabilityControllerTest
 * Description: Verifica che un medico non possa aggiungere due
 *              slot con orari sovrapposti. Il primo slot viene
 *              aggiunto con successo, mentre il secondo deve
 *              lanciare una AvailabilityException.
 * ------------------------------------------------------------
 */

class AvailabilityControllerTest {

    private AvailabilityController availabilityController;

    @BeforeEach
    void setup() {
        DemoDataStore.reset();
        DAOFactory.setPersistence(DAOFactory.MEMORY);

        Doctor doctor = new Doctor(4, "Andrea", "Neri", "dr.neri@test.com", "password", "NRNDR75C15H501Y");
        SessionManager.getInstance().setLoggedUser(doctor);

        availabilityController = new AvailabilityController();
    }

    @Test
    void testSlotSovrapposto() throws DAOException, AvailabilityException {
        // Data futura per evitare il controllo sul passato presente nel controller
        LocalDate domani = LocalDate.now().plusDays(1);

        // Primo slot — deve andare a buon fine
        TimeSlotBean slot1 = new TimeSlotBean(0, domani, LocalTime.of(9, 0), true);
        availabilityController.addSlot(slot1);

        // Secondo slot con lo stesso orario (sovrapposto) — deve lanciare AvailabilityException
        TimeSlotBean slot2 = new TimeSlotBean(0, domani, LocalTime.of(9, 0), true);

        assertThrows(AvailabilityException.class, () ->
                availabilityController.addSlot(slot2)
        );
    }
}
