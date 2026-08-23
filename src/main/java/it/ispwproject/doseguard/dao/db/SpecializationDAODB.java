package it.ispwproject.doseguard.dao.db;

import it.ispwproject.doseguard.dao.ConnectionFactory;
import it.ispwproject.doseguard.dao.SpecializationDAO;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Specialization;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecializationDAODB implements SpecializationDAO {

    private static final String GET_ALL =
            "SELECT DISTINCT DENSE_RANK() OVER (ORDER BY specialization) AS id, " +
                    "specialization AS name " +
                    "FROM doctor_detail " +
                    "WHERE specialization IS NOT NULL AND specialization != ''";

    private static final String FIND_BY_ID =
            "SELECT id, name FROM (" +
                    "  SELECT DENSE_RANK() OVER (ORDER BY specialization) AS id, " +
                    "  specialization AS name " +
                    "  FROM doctor_detail " +
                    "  WHERE specialization IS NOT NULL AND specialization != ''" +
                    ") AS spec_list WHERE id = ?";

    @Override
    public List<Specialization> getAllSpecializations() throws DAOException {
        List<Specialization> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Specialization(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento delle specializzazioni: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public Specialization findById(int id) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Specialization(rs.getInt("id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel caricamento della specializzazione: " + e.getMessage(), e);
        }
        return null;
    }

}
