package it.ispwproject.doseguard.dao.db;

import it.ispwproject.doseguard.dao.ConnectionFactory;
import it.ispwproject.doseguard.dao.UserDAO;
import it.ispwproject.doseguard.enumerator.Role;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Doctor;
import it.ispwproject.doseguard.model.Patient;
import it.ispwproject.doseguard.model.Pharmacist;
import it.ispwproject.doseguard.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAODB implements UserDAO {

    private static final String FIND_BY_EMAIL =
            "SELECT u.id, u.name, u.surname, u.email, u.password, u.role, " +
                    "COALESCE(p.fiscal_code, u.fiscal_code) AS fiscal_code, " +
                    "dd.specialization, dd.medical_license, ph.pharmacy_name " +
                    "FROM user u " +
                    "LEFT JOIN patient_detail p ON u.id = p.user_id " +
                    "LEFT JOIN doctor_detail dd ON u.id = dd.user_id " +
                    "LEFT JOIN pharmacist_detail ph ON u.id = ph.user_id " +
                    "WHERE u.email = ?";

    private static final String FIND_BY_ID =
            "SELECT u.id, u.name, u.surname, u.email, u.password, u.role, " +
                    "COALESCE(p.fiscal_code, u.fiscal_code) AS fiscal_code, " +
                    "dd.specialization, dd.medical_license, ph.pharmacy_name " +
                    "FROM user u " +
                    "LEFT JOIN patient_detail p ON u.id = p.user_id " +
                    "LEFT JOIN doctor_detail dd ON u.id = dd.user_id " +
                    "LEFT JOIN pharmacist_detail ph ON u.id = ph.user_id " +
                    "WHERE u.id = ?";

    private static final String UPDATE_EMAIL =
            "UPDATE user SET email = ? WHERE id = ?";

    private static final String GET_ALL =
            "SELECT u.id, u.name, u.surname, u.email, u.password, u.role, " +
                    "COALESCE(p.fiscal_code, u.fiscal_code) AS fiscal_code, " +
                    "dd.specialization, dd.medical_license, ph.pharmacy_name " +
                    "FROM user u " +
                    "LEFT JOIN patient_detail p ON u.id = p.user_id " +
                    "LEFT JOIN doctor_detail dd ON u.id = dd.user_id " +
                    "LEFT JOIN pharmacist_detail ph ON u.id = ph.user_id";

    @Override
    public void updateEmail(int id, String newEmail) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_EMAIL)) {
            ps.setString(1, newEmail);
            ps.setInt(2, id);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new DAOException("Utente non trovato (ID: " + id + ")");
        } catch (SQLException e) {
            throw new DAOException("Errore aggiornamento email: " + e.getMessage(), e);
        }
    }

    @Override
    public User findByEmail(String email) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_EMAIL)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new DAOException("Utente non trovato: " + email);
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            throw new DAOException("Errore caricamento utente: " + e.getMessage(), e);
        }
    }

    @Override
    public User findById(int id) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new DAOException("Utente non trovato con ID: " + id);
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            throw new DAOException("Errore caricamento utente per ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> getAll() throws DAOException {
        List<User> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore caricamento utenti: " + e.getMessage(), e);
        }
        return result;
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String surname = rs.getString("surname");
        String email = rs.getString("email");
        Role role = Role.valueOf(rs.getString("role").toUpperCase());
        String medicalLicense = rs.getString("medical_license");
        return buildUser(id, name, surname, email, role, medicalLicense);
    }

    private User buildUser(int id, String name, String surname, String email,
                           Role role, String medicalLicense) {
        return switch (role) {
            case PATIENT -> new Patient(id, name, surname, email, null, null);
            case DOCTOR  -> new Doctor(id, name, surname, email, null, medicalLicense);
            case PHARMACIST -> new Pharmacist(id, name, surname, email, null, null);
            default      -> throw new IllegalStateException("Ruolo non riconosciuto: " + role);
        };
    }

}
