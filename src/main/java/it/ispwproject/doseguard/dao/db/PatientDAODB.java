package it.ispwproject.doseguard.dao.db;

import it.ispwproject.doseguard.dao.ConnectionFactory;
import it.ispwproject.doseguard.dao.PatientDAO;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAODB implements PatientDAO {

    private static final String FIND_BY_ID =
            "SELECT u.id, u.name, u.surname, u.email, " +
                    "COALESCE(p.fiscal_code, u.fiscal_code) AS fiscal_code " +
                    "FROM user u " +
                    "LEFT JOIN patient_detail p ON u.id = p.user_id " +
                    "WHERE u.id = ? AND u.role = 'PATIENT'";

    private static final String GET_BY_DOCTOR =
            "SELECT DISTINCT u.id, u.name, u.surname, u.email, " +
                    "COALESCE(p.fiscal_code, u.fiscal_code) AS fiscal_code " +
                    "FROM user u " +
                    "LEFT JOIN patient_detail p ON u.id = p.user_id " +
                    "JOIN appointment a ON u.id = a.patient_id " +
                    "WHERE a.doctor_id = ? " +
                    "ORDER BY u.surname, u.name";

    private static final String ADD_FAVOURITE_DOCTOR =
            "INSERT IGNORE INTO patient_favourite_doctor (patient_id, doctor_id) VALUES (?, ?)";

    private static final String REMOVE_FAVOURITE_DOCTOR =
            "DELETE FROM patient_favourite_doctor WHERE patient_id = ? AND doctor_id = ?";

    private static final String IS_FAVOURITE_DOCTOR =
            "SELECT COUNT(*) FROM patient_favourite_doctor WHERE patient_id = ? AND doctor_id = ?";

    @Override
    public Patient findById(int id) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapToPatient(rs);
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento del paziente: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Patient> getByDoctor(int doctorId) throws DAOException {
        List<Patient> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_BY_DOCTOR)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapToPatient(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento dei pazienti: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public void addFavouriteDoctor(int patientId, int doctorId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(ADD_FAVOURITE_DOCTOR)) {

            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Errore nell'aggiunta del medico preferito: " + e.getMessage(), e);
        }
    }

    @Override
    public void removeFavouriteDoctor(int patientId, int doctorId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(REMOVE_FAVOURITE_DOCTOR)) {

            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException("Errore nella rimozione del medico preferito: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isFavouriteDoctor(int patientId, int doctorId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(IS_FAVOURITE_DOCTOR)) {

            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new DAOException("Errore nel controllo del medico preferito: " + e.getMessage(), e);
        }
    }

    private Patient mapToPatient(ResultSet rs) throws SQLException {
        return new Patient(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("surname"),
                rs.getString("email"),
                null, // Password omessa nei caricamenti standard
                rs.getString("fiscal_code")
        );
    }
}
