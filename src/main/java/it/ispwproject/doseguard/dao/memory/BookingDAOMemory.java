package it.ispwproject.doseguard.dao.memory;

import it.ispwproject.doseguard.dao.BookingDAO;
import it.ispwproject.doseguard.demo.DemoDataStore;
import it.ispwproject.doseguard.enumerator.AppointmentStatus;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Booking;
import it.ispwproject.doseguard.model.Patient;
import it.ispwproject.doseguard.model.TimeSlot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class BookingDAOMemory implements BookingDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public void save(Booking booking) throws DAOException {
        booking.setId(store.nextBookingId());
        booking.setStatus(AppointmentStatus.CONFIRMED);
        store.getBookings().add(booking);
        if (booking.getTimeSlot() != null) {
            booking.getTimeSlot().setAvailable(false);
        }
    }

    @Override
    public List<Booking> findByPatient(int patientId) throws DAOException {
        return store.getBookings().stream()
                .filter(b -> b.getPatient() != null && b.getPatient().getId() == patientId)
                .toList();
    }

    @Override
    public List<Booking> findByDoctor(int doctorId) throws DAOException {
        return store.getBookings().stream()
                .filter(b -> b.getDoctor() != null && b.getDoctor().getId() == doctorId
                        && b.getStatus() == AppointmentStatus.CONFIRMED)
                .toList();
    }

    @Override
    public List<Booking> findAll() throws DAOException {
        return new ArrayList<>(store.getBookings());
    }

    @Override
    public List<Booking> findCompletedBookings(int patientId, int doctorId) throws DAOException {
        return store.getBookings().stream()
                .filter(b -> b.getPatient() != null && b.getPatient().getId() == patientId
                        && b.getDoctor() != null && b.getDoctor().getId() == doctorId
                        && b.getStatus() == AppointmentStatus.CONFIRMED
                        && b.getTimeSlot() != null
                        && !b.getTimeSlot().getDate().isAfter(LocalDate.now(ZoneId.systemDefault())))
                .toList();
    }

    @Override
    public List<Booking> findUpcomingBookings(int patientId, int doctorId) throws DAOException {
        return store.getBookings().stream()
                .filter(b -> b.getPatient() != null && b.getPatient().getId() == patientId
                        && b.getDoctor() != null && b.getDoctor().getId() == doctorId
                        && b.getStatus() == AppointmentStatus.CONFIRMED
                        && b.getTimeSlot() != null
                        && b.getTimeSlot().getDate().isAfter(LocalDate.now(ZoneId.systemDefault())))
                .toList();
    }

    @Override
    public List<Booking> findPastByPatient(int patientId) throws DAOException {
        return store.getBookings().stream()
                .filter(b -> b.getPatient() != null && b.getPatient().getId() == patientId
                        && b.getStatus() == AppointmentStatus.CONFIRMED
                        && b.getTimeSlot() != null
                        && (b.getTimeSlot().getDate().isBefore(LocalDate.now(ZoneId.systemDefault())) ||
                        (b.getTimeSlot().getDate().isEqual(LocalDate.now(ZoneId.systemDefault())) &&
                                b.getTimeSlot().getStartTime().isBefore(LocalTime.now(ZoneId.systemDefault())))))
                .toList();
    }


    @Override
    public void cancel(int bookingId, int patientId) throws DAOException {
        Booking booking = store.getBookings().stream()
                .filter(b -> b.getId() == bookingId)
                .findFirst()
                .orElseThrow(() -> new DAOException("Prenotazione non trovata (ID: " + bookingId + ")"));
        Patient patient = booking.getPatient();
        if (patient == null || patient.getId() != patientId) {
            throw new DAOException("Non puoi annullare una prenotazione che non ti appartiene.");
        }
        booking.setStatus(AppointmentStatus.CANCELLED);
        TimeSlot slot = booking.getTimeSlot();
        if (slot != null) slot.setAvailable(true);
    }
}
