package it.ispwproject.doseguard.controller.applicativo;

import it.ispwproject.doseguard.bean.*;
import it.ispwproject.doseguard.dao.DAOFactory;
import it.ispwproject.doseguard.demo.DemoDataStore;
import it.ispwproject.doseguard.exception.BookingException;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Patient;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ------------------------------------------------------------
 * Test Class : BookingControllerTest
 * Description: Verifica il meccanismo di gestione delle prenotazioni,
 *              testando l'eccezione quando si tenta di impegnare
 *              uno slot già riservato da un altro utente.
 * ------------------------------------------------------------
 */
class BookingControllerTest {

    @BeforeEach
    void setup() {
        DemoDataStore.reset();
        DAOFactory.setPersistence(DAOFactory.MEMORY);
    }

    @Test
    void testPrenotazioneSuSlotGiaRiservato() throws DAOException, BookingException {
        // Preparazione degli oggetti necessari per la richiesta di prenotazione
        TimeSlotBean slotBean = new TimeSlotBean();
        slotBean.setId(3);

        DoctorBean doctorBean = new DoctorBean();
        doctorBean.setId(4); // ID medico dai dati di test (Andrea Neri)

        SpecializationBean specBean = new SpecializationBean();
        specBean.setId(1); // Specifica un id valido per la specializzazione

        // Paziente 1 (Mario Rossi)
        Patient p1 = new Patient(1, "Mario", "Rossi", "mario.rossi@test.com", "password", "RSSMRA80A01H501U");
        SessionManager.getInstance().setLoggedUser(p1);

        PatientBean patientBean1 = new PatientBean(1, "Mario", "Rossi", "mario.rossi@test.com", "RSSMRA80A01H501U");
        AppointmentRequestBean request1 = new AppointmentRequestBean(patientBean1, doctorBean, specBean, slotBean, "Visita di controllo");

        BookingController bc1 = new BookingController();

        // Primo tentativo: riserva lo slot con successo
        bc1.prepareBookingSummary(request1);

        // Paziente 2 (Laura Bianchi) tenta di prenotare lo stesso slot
        Patient p2 = new Patient(2, "Laura", "Bianchi", "laura.bianchi@test.com", "password", "BNCLRA85M42F205Z");
        SessionManager.getInstance().setLoggedUser(p2);

        PatientBean patientBean2 = new PatientBean(2, "Laura", "Bianchi", "laura.bianchi@test.com", "BNCLRA85M42F205Z");
        AppointmentRequestBean request2 = new AppointmentRequestBean(patientBean2, doctorBean, specBean, slotBean, "Visita urgente");

        BookingController bc2 = new BookingController();

        // Secondo tentativo: deve sollevare BookingException perché lo slot è già occupato
        assertThrows(BookingException.class, () ->
                bc2.prepareBookingSummary(request2)
        );
    }

    @Test
    void testPrenotazioneSlotInesistente() {
        Patient p1 = new Patient(1, "Mario", "Rossi", "mario.rossi@test.com", "password", "RSSMRA80A01H501U");
        SessionManager.getInstance().setLoggedUser(p1);

        TimeSlotBean slotInesistente = new TimeSlotBean();
        slotInesistente.setId(9999); // ID inesistente

        DoctorBean doctorBean = new DoctorBean();
        doctorBean.setId(4);

        SpecializationBean specBean = new SpecializationBean();
        specBean.setId(1);

        PatientBean patientBean = new PatientBean(1, "Mario", "Rossi", "mario.rossi@test.com", "RSSMRA80A01H501U");
        AppointmentRequestBean request = new AppointmentRequestBean(patientBean, doctorBean, specBean, slotInesistente, "Controllo");

        BookingController bookingController = new BookingController();

        // Tentare di prenotare uno slot inesistente deve lanciare BookingException (o DAOException gestita)
        assertThrows(Exception.class, () ->
                bookingController.createBooking(request)
        );
    }

}