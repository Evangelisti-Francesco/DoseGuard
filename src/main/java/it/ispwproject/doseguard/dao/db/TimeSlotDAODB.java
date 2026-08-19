package it.ispwproject.doseguard.dao.db;

import it.ispwproject.doseguard.dao.ConnectionFactory;
import it.ispwproject.doseguard.dao.TimeSlotDAO;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Doctor;
import it.ispwproject.doseguard.model.TimeSlot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotDAODB implements TimeSlotDAO {

    private static final String GET_AVAILABLE_BY_DOCTOR =
            "SELECT id, date, start_time, end_time, available " +
                    "FROM time_slot WHERE doctor_id = ? AND available = TRUE " +
                    "AND (date > CURDATE() OR (date = CURDATE() AND start_time > CURTIME())) " +
                    "AND (reserved_until IS NULL OR reserved_until < NOW()) " +
                    "ORDER BY date, start_time";

    private static final String GET_ALL_BY_DOCTOR =
            "SELECT id, date, start_time, end_time, available " +
                    "FROM time_slot " +
                    "WHERE doctor_id = ? " +
                    "AND (date > CURDATE() OR (date = CURDATE() AND start_time > CURTIME())) " +
                    "ORDER BY date, start_time";

    private static final String FIND_BY_ID =
            "SELECT id, date, start_time, end_time, available FROM time_slot WHERE id = ?";

    private static final String SAVE =
            "INSERT INTO time_slot (doctor_id, date, start_time, end_time, available) VALUES (?, ?, ?, ?, TRUE)";

    private static final String RESERVE_SLOT = "{call reserve_slot(?, ?, ?)}";
    private static final String RELEASE_SLOT = "{call release_slot(?)}";
    private static final String DELETE_SLOT =  "DELETE FROM time_slot WHERE id = ? AND doctor_id = ? AND available = TRUE";

    @Override
    public boolean reserveSlot(int slotId, int minutes) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall(RESERVE_SLOT)) {
            cs.setInt(1, slotId);
            cs.setInt(2, minutes);
            cs.registerOutParameter(3, java.sql.Types.BOOLEAN);
            cs.execute();
            return cs.getBoolean(3);
        } catch (SQLException e) {
            throw new DAOException("Errore durante la prenotazione temporanea: " + e.getMessage(), e);
        }
    }

    @Override
    public void releaseSlot(int slotId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall(RELEASE_SLOT)) {
            cs.setInt(1, slotId);
            cs.execute();
        } catch (SQLException e) {
            throw new DAOException("Errore durante il rilascio dello slot: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TimeSlot> getAvailableByDoctor(Doctor doctor) throws DAOException {
        List<TimeSlot> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_AVAILABLE_BY_DOCTOR)) {
            ps.setInt(1, doctor.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapToTimeSlot(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento degli slot: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<TimeSlot> getAllByDoctor(int doctorId) throws DAOException {
        List<TimeSlot> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_ALL_BY_DOCTOR)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TimeSlot slot = new TimeSlot(
                            rs.getInt("id"),
                            rs.getDate("date").toLocalDate(),
                            rs.getTime("start_time").toLocalTime());
                    slot.setAvailable(rs.getBoolean("available"));
                    result.add(slot);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento degli slot: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public TimeSlot findById(int id) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapToTimeSlot(rs);
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento dello slot: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void save(TimeSlot slot, int doctorId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, Date.valueOf(slot.getDate()));
            ps.setTime(3, Time.valueOf(slot.getStartTime()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) slot.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore durante il salvataggio dello slot: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int slotId, int doctorId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SLOT)) {
            ps.setInt(1, slotId);
            ps.setInt(2, doctorId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new DAOException("Slot non trovato o già prenotato.");
        } catch (SQLException e) {
            throw new DAOException("Errore eliminazione slot: " + e.getMessage(), e);
        }
    }

    private TimeSlot mapToTimeSlot(ResultSet rs) throws SQLException {
        TimeSlot slot = new TimeSlot(
                rs.getInt("id"),
                rs.getDate("date").toLocalDate(),
                rs.getTime("start_time").toLocalTime());
        slot.setAvailable(rs.getBoolean("available"));
        return slot;
    }
}
