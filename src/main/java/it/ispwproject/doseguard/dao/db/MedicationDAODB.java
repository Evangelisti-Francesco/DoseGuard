package it.ispwproject.doseguard.dao.db;

import it.ispwproject.doseguard.dao.ConnectionFactory;
import it.ispwproject.doseguard.dao.MedicationDAO;
import it.ispwproject.doseguard.dao.PatientDAO;
import it.ispwproject.doseguard.dao.DAOFactory;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Medication;
import it.ispwproject.doseguard.model.Patient;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MedicationDAODB implements MedicationDAO {

    private static final String GET_BY_PATIENT =
            "SELECT ts.id, ts.patient_id, ts.scheduled_time, ts.taken, " +
                    "       COALESCE(p.drug, 'Farmaco') AS drug_name, COALESCE(p.dosage, '') AS dosage " +
                    "FROM therapy_schedule ts " +
                    "LEFT JOIN prescription p ON ts.prescription_id = p.id " +
                    "WHERE ts.patient_id = ? " +
                    "ORDER BY ts.scheduled_time ASC";

    private static final String INSERT_MEDICATION =
            "INSERT INTO therapy_schedule (patient_id, medication_name, dosage, scheduled_time, taken) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String MARK_AS_TAKEN =
            "UPDATE therapy_schedule SET taken = TRUE WHERE id = ? AND patient_id = ?";

    private static final String DELETE_MEDICATION =
            "DELETE FROM therapy_schedule WHERE id = ? AND patient_id = ?";

    private final PatientDAO patientDAO;

    public MedicationDAODB() {
        this.patientDAO = DAOFactory.getPatientDAO();
    }

    @Override
    public List<Medication> getByPatient(int patientId) throws DAOException {
        List<Medication> result = new ArrayList<>();

        // Recuperiamo prima l'oggetto Patient per non aprire query nidificate mentre si scorre il ResultSet
        Patient patient = null;
        try {
            patient = patientDAO.findById(patientId);
        } catch (DAOException ignored) {
            // Se fallisce il recupero del paziente completo, usiamo un oggetto stub per non bloccare i farmaci
            patient = new Patient(patientId, "", "", "", "", null);
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_BY_PATIENT)) {

            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToMedication(rs, patient));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore SQL durante il recupero dei farmaci del paziente: " + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public void save(Medication medication) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_MEDICATION, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, medication.getPatient().getId());
            stmt.setString(2, medication.getName());
            stmt.setString(3, medication.getDosage());
            stmt.setTime(4, Time.valueOf(medication.getScheduleTime()));
            stmt.setBoolean(5, medication.isTaken());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    medication.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore durante il salvataggio del farmaco: " + e.getMessage(), e);
        }
    }

    @Override
    public void markAsTaken(int medicationId, int patientId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(MARK_AS_TAKEN)) {

            stmt.setInt(1, medicationId);
            stmt.setInt(2, patientId);
            int updatedRows = stmt.executeUpdate();

            if (updatedRows == 0) {
                throw new DAOException("Farmaco non trovato o non associato al paziente specificato.");
            }
        } catch (SQLException e) {
            throw new DAOException("Errore durante la registrazione dell'assunzione del farmaco: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int medicationId, int patientId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_MEDICATION)) {

            stmt.setInt(1, medicationId);
            stmt.setInt(2, patientId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Errore durante l'eliminazione del farmaco: " + e.getMessage(), e);
        }
    }

    private Medication mapResultSetToMedication(ResultSet rs, Patient patient) throws SQLException {
        int id = rs.getInt("id");

        String name = rs.getString("drug_name");
        String dosage = rs.getString("dosage");

        Timestamp timestamp = rs.getTimestamp("scheduled_time");
        LocalTime scheduleTime = (timestamp != null) ? timestamp.toLocalDateTime().toLocalTime() : LocalTime.MIDNIGHT;
        boolean taken = rs.getBoolean("taken");

        return new Medication(id, patient, name, dosage, scheduleTime, taken);
    }
}
