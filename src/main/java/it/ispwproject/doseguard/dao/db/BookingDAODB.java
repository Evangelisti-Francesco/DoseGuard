package it.ispwproject.doseguard.dao.db;

import it.ispwproject.doseguard.dao.AbstractBookingDAO;
import it.ispwproject.doseguard.dao.ConnectionFactory;
import it.ispwproject.doseguard.enumerator.AppointmentStatus;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.ResourceBundle.clearCache;

public class BookingDAODB extends AbstractBookingDAO {

    private static final String INSERT_BOOKING =
            "INSERT INTO appointment (patient_id, doctor_id, slot_id, status) " +
                    "VALUES (?, ?, ?, 'CONFIRMED')";

    private static final String CANCEL_BOOKING =
            "UPDATE appointment SET status = 'CANCELLED' WHERE id = ? AND patient_id = ?";

    private static final String FREE_SLOT =
            "UPDATE time_slot SET available = TRUE " +
                    "WHERE id = (SELECT slot_id FROM appointment WHERE id = ? AND patient_id = ?)";

    private static final String UPDATE_SLOT_AVAILABILITY =
            "UPDATE time_slot SET available = ? WHERE id = ?";

    private static final String SELECT_BOOKINGS =
            "SELECT a.id, '' AS notes, a.status, " +
                    "       u_p.id p_id, u_p.name p_name, u_p.surname p_surname, u_p.email p_email, " +
                    "       u_d.id d_id, u_d.name d_name, u_d.surname d_surname, u_d.email d_email, " +
                    "       0 AS spec_id, dd.specialization AS spec_name, " +
                    "       ts.id ts_id, ts.date ts_date, ts.start_time, ts.available " +
                    "FROM appointment a " +
                    "JOIN user u_p ON a.patient_id = u_p.id " +
                    "JOIN user u_d ON a.doctor_id = u_d.id " +
                    "LEFT JOIN doctor_detail dd ON u_d.id = dd.user_id " +
                    "JOIN time_slot ts ON a.slot_id = ts.id ";

    private static final String FIND_BY_PATIENT = SELECT_BOOKINGS +
            "WHERE a.patient_id = ? ORDER BY ts.date DESC";

    private static final String FIND_BY_DOCTOR = SELECT_BOOKINGS +
            "WHERE a.doctor_id = ? AND a.status = 'CONFIRMED' ORDER BY ts.date ASC";

    private static final String FIND_ALL = SELECT_BOOKINGS +
            "ORDER BY ts.date DESC";

    private static final String FIND_COMPLETED = SELECT_BOOKINGS +
            "WHERE a.patient_id = ? AND a.doctor_id = ? " +
            "  AND a.status = 'CONFIRMED' AND ts.date <= CURDATE() " +
            "ORDER BY ts.date DESC";

    private static final String FIND_UPCOMING = SELECT_BOOKINGS +
            "WHERE a.patient_id = ? AND a.doctor_id = ? " +
            "  AND a.status = 'CONFIRMED' AND ts.date > CURDATE() " +
            "ORDER BY ts.date ASC";

    private static final String FIND_PAST_BY_PATIENT = SELECT_BOOKINGS +
            "WHERE a.patient_id = ? AND a.status = 'CONFIRMED' " +
            "AND (ts.date < CURDATE() OR (ts.date = CURDATE() AND ts.start_time < CURTIME())) " +
            "ORDER BY ts.date DESC, ts.start_time DESC";


    @Override
    public void save(Booking booking) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO appointment (patient_id, doctor_id, slot_id, status) VALUES (?, ?, ?, 'CONFIRMED')",
                     Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, booking.getPatient().getId());
            ps.setInt(2, booking.getDoctor().getId());
            ps.setInt(3, booking.getTimeSlot().getId());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) booking.setId(keys.getInt(1));
            }

            booking.setStatus(AppointmentStatus.CONFIRMED);

            // Pulisce la cache locale per costringere il DAO a ricaricare i dati aggiornati dal DB
            clearCache();
        } catch (SQLException e) {
            throw new DAOException("Errore durante il salvataggio della prenotazione: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Booking> findByPatient(int patientId) throws DAOException {
        List<Booking> cached = findInCacheByPatient(patientId);
        if (!cached.isEmpty()) return cached;
        List<Booking> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_PATIENT)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking b = mapToBooking(rs);
                    addToCache(b);
                    result.add(b);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento prenotazioni: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<Booking> findByDoctor(int doctorId) throws DAOException {
        List<Booking> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_DOCTOR)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapToBooking(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento prenotazioni medico: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<Booking> findAll() throws DAOException {
        List<Booking> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(mapToBooking(rs));
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento di tutte le prenotazioni: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<Booking> findCompletedBookings(int patientId, int doctorId) throws DAOException {
        List<Booking> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_COMPLETED)) {
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapToBooking(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento prenotazioni completate: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<Booking> findUpcomingBookings(int patientId, int doctorId) throws DAOException {
        List<Booking> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_UPCOMING)) {
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapToBooking(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento prenotazioni future: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<Booking> findPastByPatient(int patientId) throws DAOException {
        List<Booking> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_PAST_BY_PATIENT)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapToBooking(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel recupero delle visite passate: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public void cancel(int bookingId, int patientId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            executeCancel(conn, bookingId, patientId);
            conn.commit();
            conn.setAutoCommit(true);
            updateInCache(bookingId);
            identityMap.removeIf(b ->
                    b.getPatient() != null && b.getPatient().getId() == patientId);
        } catch (SQLException e) {
            throw new DAOException("Errore durante l'annullamento: " + e.getMessage(), e);
        }
    }

    private void executeCancel(Connection conn, int bookingId,
                               int patientId) throws SQLException, DAOException {
        freeSlot(conn, bookingId, patientId);
        cancelBooking(conn, bookingId, patientId);
    }

    private void freeSlot(Connection conn, int bookingId, int patientId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(FREE_SLOT)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, patientId);
            ps.executeUpdate();
        }
    }

    private void cancelBooking(Connection conn, int bookingId,
                               int patientId) throws SQLException, DAOException {
        try (PreparedStatement ps = conn.prepareStatement(CANCEL_BOOKING)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, patientId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new DAOException("Prenotazione non trovata o non autorizzata.");
        }
    }

    private void updateSlotAvailability(Connection conn, int slotId,
                                        boolean available) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_SLOT_AVAILABILITY)) {
            ps.setBoolean(1, available);
            ps.setInt(2, slotId);
            ps.executeUpdate();
        }
    }

    private Booking mapToBooking(ResultSet rs) throws SQLException {
        Patient patient = new Patient(
                rs.getInt("p_id"),
                rs.getString("p_name"),
                rs.getString("p_surname"),
                rs.getString("p_email"),
                null,
                null);

        Doctor doctor = new Doctor(
                rs.getInt("d_id"),
                rs.getString("d_name"),
                rs.getString("d_surname"),
                rs.getString("d_email"),
                null,
                null);

        Specialization spec = new Specialization(
                rs.getInt("spec_id"),
                rs.getString("spec_name"));

        TimeSlot slot = new TimeSlot(
                rs.getInt("ts_id"),
                doctor,
                rs.getDate("ts_date").toLocalDate(),
                rs.getTime("start_time").toLocalTime());
        slot.setAvailable(rs.getBoolean("available"));

        Booking booking = new Booking(patient, doctor, spec, slot, rs.getString("notes"));
        booking.setId(rs.getInt("id"));
        booking.setStatus(AppointmentStatus.valueOf(rs.getString("status")));
        return booking;
    }
}
