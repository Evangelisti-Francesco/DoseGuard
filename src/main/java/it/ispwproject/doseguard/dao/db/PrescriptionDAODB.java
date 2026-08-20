package it.ispwproject.doseguard.dao.db;

import it.ispwproject.doseguard.dao.*;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Doctor;
import it.ispwproject.doseguard.model.Patient;
import it.ispwproject.doseguard.model.Prescription;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionDAODB implements PrescriptionDAO {

    private static final String INSERT_PRESCRIPTION = "INSERT INTO prescription (doctor_id, patient_id, pharmacist_id, drug, dosage, frequency, issue_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String GET_BY_PATIENT_FISCAL_CODE = "SELECT p.id, p.doctor_id, p.patient_id, p.pharmacist_id, p.drug, p.dosage, p.frequency, p.issue_date FROM prescription p JOIN patient pt ON p.patient_id = pt.id WHERE pt.fiscal_code = ?";
    private static final String FIND_BY_ID = "SELECT id, doctor_id, patient_id, pharmacist_id, drug, dosage, frequency, issue_date FROM prescription WHERE id = ?";
    private static final String MARK_AS_FULFILLED = "UPDATE prescription SET pharmacist_id = ? WHERE id = ?";

    private final DoctorDAO doctorDAO;
    private final PatientDAO patientDAO;

    public PrescriptionDAODB() {
        this.doctorDAO = DAOFactory.getDoctorDAO();
        this.patientDAO = DAOFactory.getPatientDAO();
    }

    @Override
    public void save(Prescription prescription) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_PRESCRIPTION, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, prescription.getDoctor().getId());
            stmt.setInt(2, prescription.getPatient().getId());
            if (prescription.getPharmacistId() != null) {
                stmt.setInt(3, prescription.getPharmacistId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setString(4, prescription.getDrug());
            stmt.setString(5, prescription.getDosage());
            stmt.setString(6, prescription.getFrequency());
            stmt.setDate(7, Date.valueOf(prescription.getIssueDate()));

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    prescription.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore durante il salvataggio della ricetta.", e);
        }
    }

    @Override
    public List<Prescription> getByPatientFiscalCode(String fiscalCode) throws DAOException {
        List<Prescription> result = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_BY_PATIENT_FISCAL_CODE)) {

            stmt.setString(1, fiscalCode);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToPrescription(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore durante il recupero delle ricette del paziente.", e);
        }
        return result;
    }

    @Override
    public Prescription findById(int prescriptionId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID)) {

            stmt.setInt(1, prescriptionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPrescription(rs);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore durante la ricerca della ricetta.", e);
        }
        return null;
    }

    @Override
    public void markAsFulfilled(int prescriptionId, int pharmacistId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(MARK_AS_FULFILLED)) {

            stmt.setInt(1, pharmacistId);
            stmt.setInt(2, prescriptionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Errore durante la marcatura della ricetta come erogata.", e);
        }
    }

    private Prescription mapResultSetToPrescription(ResultSet rs) throws SQLException, DAOException {
        int doctorId = rs.getInt("doctor_id");
        int patientId = rs.getInt("patient_id");

        String drug = rs.getString("drug");
        String dosage = rs.getString("dosage");
        String frequency = rs.getString("frequency");
        Date issueDate = rs.getDate("issue_date");

        Doctor doctor = doctorDAO.findById(doctorId);
        Patient patient = patientDAO.findById(patientId);

        return new Prescription(doctor, patient, drug, dosage, frequency,issueDate.toLocalDate());
    }
}