package it.ispwproject.doseguard.dao.db;

import it.ispwproject.doseguard.dao.ConnectionFactory;
import it.ispwproject.doseguard.dao.RegistrationDAO;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Doctor;
import it.ispwproject.doseguard.model.Patient;
import it.ispwproject.doseguard.model.Pharmacist;
import it.ispwproject.doseguard.model.User;
import it.ispwproject.doseguard.util.logger.AppLogger;

import java.sql.*;

public class RegistrationDAODB implements RegistrationDAO {

    private static final String CLEAR_ROLE_FAILED = "clearRole fallito: ";

    private static final String INSERT_USER =
            "INSERT INTO user (name, surname, fiscal_code, email, password, role) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String INSERT_PATIENT_DETAIL =
            "INSERT INTO patient_detail (user_id, fiscal_code) VALUES (?, ?)";

    private static final String INSERT_DOCTOR_DETAIL =
            "INSERT INTO doctor_detail (user_id, specialization) VALUES (?, ?)";

    private static final String INSERT_PHARMACIST_DETAIL =
            "INSERT INTO pharmacist_detail (user_id, pharmacy_name) VALUES (?, ?)";

    private static final String CHECK_EMAIL =
            "SELECT COUNT(*) FROM user WHERE email = ?";

    private static final String CHECK_FISCAL_CODE =
            "SELECT COUNT(*) FROM user WHERE fiscal_code = ?";

    @Override
    public boolean emailExists(String email) throws DAOException {
        try { ConnectionFactory.clearRole(); }
        catch (SQLException e) { AppLogger.logWarning(CLEAR_ROLE_FAILED + e.getMessage()); }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(CHECK_EMAIL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Errore verifica email: " + e.getMessage(),e);
        }

        return false;
    }

    @Override
    public boolean fiscalCodeExists(String fiscalCode) throws DAOException {
        try { ConnectionFactory.clearRole(); }
        catch (SQLException e) { AppLogger.logWarning(CLEAR_ROLE_FAILED + e.getMessage()); }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(CHECK_FISCAL_CODE)) {

            ps.setString(1, fiscalCode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore verifica codice fiscale: " + e.getMessage(),
                    e
            );
        }

        return false;
    }

    @Override
    public void saveUser(User user) throws DAOException {
        try { ConnectionFactory.clearRole(); }
        catch (SQLException e) { AppLogger.logWarning(CLEAR_ROLE_FAILED + e.getMessage()); }

        try (Connection conn = ConnectionFactory.getConnection()) {

            executeSaveTransaction(conn, user);

        } catch (SQLException e) {

            throw new DAOException("Errore connessione: " + e.getMessage(),e);
        }
    }

    private void executeSaveTransaction(Connection conn, User user)
            throws SQLException, DAOException {

        conn.setAutoCommit(false);

        try {

            int userId = insertUser(conn, user);
            user.setId(userId);

            if (user instanceof Patient patient) {
                insertPatientDetail(conn, userId, patient.getFiscalCode());
            } else if (user instanceof Doctor doctor) {
                insertDoctorDetail(conn, userId, doctor.getSpecialization());
            } else if (user instanceof Pharmacist pharmacist) {
                insertPharmacistDetail(conn, userId, pharmacist.getPharmacyName());
            }

            conn.commit();

        } catch (SQLException e) {

            conn.rollback();

            throw new DAOException("Errore registrazione: " + e.getMessage(),e);

        } finally {

            conn.setAutoCommit(true);
        }
    }

    private int insertUser(Connection conn, User user) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(
                INSERT_USER,
                Statement.RETURN_GENERATED_KEYS)) {

            // Estraiamo il codice fiscale SOLO se l'utente è un Paziente
            String fiscalCode = null;
            if (user instanceof Patient patient) {
                fiscalCode = patient.getFiscalCode();
            }

            ps.setString(1, user.getName());
            ps.setString(2, user.getSurname());

            // Se è un paziente mettiamo il codice fiscale
            if (fiscalCode != null && !fiscalCode.isEmpty()) {
                ps.setString(3, fiscalCode);
            } else {
                ps.setNull(3, Types.VARCHAR);
            }

            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPassword());
            ps.setString(6, user.getRole().name());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException("ID utente non generato.");
    }

    private void insertPatientDetail(Connection conn, int patientId, String fiscalCode) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(INSERT_PATIENT_DETAIL)) {

            ps.setInt(1, patientId);
            ps.setString(2, fiscalCode);

            ps.executeUpdate();
        }
    }

    private void insertDoctorDetail(Connection conn, int doctorId, String specialization) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(INSERT_DOCTOR_DETAIL)) {

            ps.setInt(1, doctorId);
            ps.setString(2, specialization);

            ps.executeUpdate();
        }
    }

    private void insertPharmacistDetail(Connection conn, int pharmacistId, String pharmacyName) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(INSERT_PHARMACIST_DETAIL)) {

            ps.setInt(1, pharmacistId);
            ps.setString(2, pharmacyName);

            ps.executeUpdate();
        }
    }
}
