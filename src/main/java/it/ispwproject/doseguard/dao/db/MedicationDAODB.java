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

    private static final String GET_BY_PATIENT = "SELECT id, patient_id, name, dosage, schedule_time, taken_today FROM medication WHERE patient_id = ?";
    private static final String INSERT_MEDICATION = "INSERT INTO medication (patient_id, name, dosage, schedule_time, taken_today) VALUES (?, ?, ?, ?, ?)";
    private static final String MARK_AS_TAKEN = "UPDATE medication SET taken_today = true WHERE id = ? AND patient_id = ?";
    private static final String DELETE_MEDICATION = "DELETE FROM medication WHERE id = ? AND patient_id = ?";

    private final PatientDAO patientDAO;

    public MedicationDAODB() {
        this.patientDAO = DAOFactory.getPatientDAO();
    }

    @Override
    public List<Medication> getByPatient(int patientId) throws DAOException {
        List<Medication> result = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_BY_PATIENT)) {

            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToMedication(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore durante il recupero dei farmaci del paziente.", e);
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
            throw new DAOException("Errore durante il salvataggio del farmaco.", e);
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
            throw new DAOException("Errore durante la registrazione dell'assunzione del farmaco.", e);
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
            throw new DAOException("Errore durante l'eliminazione del farmaco.", e);
        }
    }

    private Medication mapResultSetToMedication(ResultSet rs) throws SQLException, DAOException {
        int id = rs.getInt("id");
        int patientId = rs.getInt("patient_id");
        String name = rs.getString("name");
        String dosage = rs.getString("dosage");
        Time time = rs.getTime("schedule_time");
        LocalTime scheduleTime = (time != null) ? time.toLocalTime() : null;
        boolean takenToday = rs.getBoolean("taken_today");

        Patient patient = patientDAO.findById(patientId);

        return new Medication(id, patient, name, dosage, scheduleTime, takenToday);
    }
}
