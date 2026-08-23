package it.ispwproject.doseguard.dao.db;

import it.ispwproject.doseguard.dao.ConnectionFactory;
import it.ispwproject.doseguard.dao.DoctorDAO;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Doctor;
import it.ispwproject.doseguard.model.Specialization;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAODB implements DoctorDAO {

    private static final String FIND_BY_ID =
            "SELECT u.id, u.name, u.surname, u.email, dd.specialization, dd.medical_license " +
                    "FROM user u " +
                    "LEFT JOIN doctor_detail dd ON u.id = dd.user_id " +
                    "WHERE u.id = ? AND u.role = 'DOCTOR'";

    private static final String GET_ALL_DOCTORS =
            "SELECT u.id, u.name, u.surname, u.email, dd.specialization, dd.medical_license " +
                    "FROM user u " +
                    "LEFT JOIN doctor_detail dd ON u.id = dd.user_id " +
                    "WHERE u.role = 'DOCTOR' " +
                    "ORDER BY u.surname, u.name";

    private static final String GET_BY_SPECIALIZATION =
            "SELECT u.id, u.name, u.surname, u.email, dd.specialization, dd.medical_license " +
                    "FROM user u " +
                    "LEFT JOIN doctor_detail dd ON u.id = dd.user_id " +
                    "WHERE u.role = 'DOCTOR' AND dd.specialization IN (" +
                    "   SELECT name FROM (" +
                    "       SELECT DENSE_RANK() OVER (ORDER BY specialization) AS spec_id, specialization AS name " +
                    "       FROM doctor_detail WHERE specialization IS NOT NULL AND specialization != ''" +
                    "   ) AS specs WHERE spec_id = ?" +
                    ") ORDER BY u.surname, u.name";
    @Override
    public List<Doctor> getBySpecialization(Specialization specialization) throws DAOException {
        List<Doctor> result = new ArrayList<>();
        if (specialization == null) return result;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_BY_SPECIALIZATION)) {
            ps.setInt(1, specialization.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapToDoctor(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento dei medici per specializzazione: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<Doctor> getAllDoctors() throws DAOException {
        List<Doctor> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_ALL_DOCTORS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(mapToDoctor(rs));
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento di tutti i medici: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public Doctor findById(int id) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapToDoctor(rs);
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento del medico: " + e.getMessage(), e);
        }
        return null;
    }

    private Doctor mapToDoctor(ResultSet rs) throws SQLException {
        return new Doctor(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("surname"),
                rs.getString("email"),
                null, // Password omessa nei caricamenti da DAO
                rs.getString("specialization")
        );
    }
}
