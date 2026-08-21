package it.ispwproject.doseguard.dao.db;

import it.ispwproject.doseguard.dao.ConnectionFactory;
import it.ispwproject.doseguard.dao.ProgressDAO;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Doctor;
import it.ispwproject.doseguard.model.Patient;
import it.ispwproject.doseguard.model.PatientProgress;

import java.sql.*;

public class ProgressDAODB implements ProgressDAO {

    private static final String SAVE =
            "INSERT INTO patient_progress (doctor_id, patient_id, notes) VALUES (?, ?, ?)";

    private static final String UPDATE =
            "UPDATE patient_progress SET notes = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE doctor_id = ? AND patient_id = ?";

    private static final String FIND_BY_PATIENT_AND_DOCTOR =
            "SELECT p.id, p.notes, p.updated_at, " +
                    "       u_d.id d_id, u_d.name d_name, u_d.surname d_surname, " +
                    "       u_p.id p_id, u_p.name p_name, u_p.surname p_surname, u_p.email p_email " +
                    "FROM patient_progress p " +
                    "JOIN user u_d ON p.doctor_id  = u_d.id " +
                    "JOIN user u_p ON p.patient_id = u_p.id " +
                    "WHERE p.doctor_id = ? AND p.patient_id = ?";

    @Override
    public void saveOrUpdate(PatientProgress progress) throws DAOException {
        PatientProgress existing = findByPatientAndDoctor(
                progress.getDoctor().getId(), progress.getPatient().getId());
        if (existing == null) save(progress);
        else update(progress);
    }

    private void save(PatientProgress progress) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, progress.getDoctor().getId());
            ps.setInt(2, progress.getPatient().getId());
            ps.setString(3, progress.getNotes());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) progress.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore salvataggio progresso: " + e.getMessage(), e);
        }
    }

    private void update(PatientProgress progress) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setString(1, progress.getNotes());
            ps.setInt(2, progress.getDoctor().getId());
            ps.setInt(3, progress.getPatient().getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Errore aggiornamento progresso: " + e.getMessage(), e);
        }
    }

    @Override
    public PatientProgress findByPatientAndDoctor(int doctorId, int patientId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_PATIENT_AND_DOCTOR)) {
            ps.setInt(1, doctorId);
            ps.setInt(2, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Doctor doctor = new Doctor(rs.getInt("d_id"), rs.getString("d_name"),
                            rs.getString("d_surname"), null, null, null);
                    Patient patient = new Patient(rs.getInt("p_id"), rs.getString("p_name"),
                            rs.getString("p_surname"), rs.getString("p_email"), null,null);
                    PatientProgress p = new PatientProgress(doctor, patient, rs.getString("notes"));
                    p.setId(rs.getInt("id"));
                    p.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    return p;
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore caricamento progresso: " + e.getMessage(), e);
        }
        return null;
    }
}
