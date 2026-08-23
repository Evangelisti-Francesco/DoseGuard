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

    private static final String INSERT_PRESCRIPTION =
            "INSERT INTO prescription (doctor_id, patient_id, drug, dosage, frequency, issue_date, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, 'PENDING')";

    private static final String GET_BY_PATIENT_FISCAL_CODE =
            "SELECT p.id, p.doctor_id, p.patient_id, p.drug, p.dosage, p.frequency, p.issue_date, p.status, " +
                    "       u_p.name AS p_name, u_p.surname AS p_surname, u_p.email AS p_email, " +
                    "       u_d.name AS d_name, u_d.surname AS d_surname, u_d.email AS d_email " +
                    "FROM prescription p " +
                    "JOIN user u_p ON p.patient_id = u_p.id " +
                    "JOIN user u_d ON p.doctor_id = u_d.id " +
                    "LEFT JOIN patient_detail pd ON u_p.id = pd.user_id " +
                    "WHERE pd.fiscal_code = ? OR u_p.email = ?";

    private static final String FIND_BY_ID =
            "SELECT p.id, p.doctor_id, p.patient_id, p.drug, p.dosage, p.frequency, p.issue_date, p.status " +
                    "FROM prescription p WHERE p.id = ?";

    private static final String MARK_AS_FULFILLED =
            "UPDATE prescription SET status = 'DISPENSED' WHERE id = ?";

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
            stmt.setString(3, prescription.getDrug());
            stmt.setString(4, prescription.getDosage());
            stmt.setString(5, prescription.getFrequency());
            stmt.setDate(6, Date.valueOf(prescription.getIssueDate()));

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    prescription.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore durante il salvataggio della ricetta: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Prescription> getByPatientFiscalCode(String fiscalCode) throws DAOException {
        List<Prescription> result = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_BY_PATIENT_FISCAL_CODE)) {

            stmt.setString(1, fiscalCode);
            stmt.setString(2, fiscalCode);

            // 1. Leggiamo ed estraiamo tutti i dati dal ResultSet in memoria
            List<PrescriptionDataHolder> rawDataList = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rawDataList.add(new PrescriptionDataHolder(
                            rs.getInt("id"),
                            rs.getInt("doctor_id"),
                            rs.getInt("patient_id"),
                            rs.getString("drug"),
                            rs.getString("dosage"),
                            rs.getString("frequency"),
                            rs.getDate("issue_date")
                    ));
                }
            } // Il ResultSet si chiude in sicurezza qui

            // 2. Mappiamo gli oggetti e richiamiamo gli altri DAO fuori dal ResultSet
            for (PrescriptionDataHolder data : rawDataList) {
                Doctor doctor = doctorDAO.findById(data.doctorId);
                Patient patient = patientDAO.findById(data.patientId);

                Prescription prescription = new Prescription(
                        doctor,
                        patient,
                        data.drug,
                        data.dosage,
                        data.frequency,
                        data.issueDate != null ? data.issueDate.toLocalDate() : java.time.LocalDate.now(java.time.ZoneId.systemDefault())
                );
                prescription.setId(data.id);
                result.add(prescription);
            }

        } catch (SQLException e) {
            throw new DAOException("Errore durante il recupero delle ricette del paziente: " + e.getMessage(), e);
        }
        return result;
    }

    // Classe di supporto interna (in coda a PrescriptionDAODB.java)
    private static class PrescriptionDataHolder {
        int id;
        int doctorId;
        int patientId;
        String drug;
        String dosage;
        String frequency;
        Date issueDate;

        PrescriptionDataHolder(int id, int doctorId, int patientId, String drug, String dosage, String frequency, Date issueDate) {
            this.id = id;
            this.doctorId = doctorId;
            this.patientId = patientId;
            this.drug = drug;
            this.dosage = dosage;
            this.frequency = frequency;
            this.issueDate = issueDate;
        }
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
            throw new DAOException("Errore durante la ricerca della ricetta: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void markAsFulfilled(int prescriptionId, int pharmacistId) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(MARK_AS_FULFILLED)) {

            // La query accetta solo 1 parametro (prescriptionId)
            stmt.setInt(1, prescriptionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Errore durante la marcatura della ricetta come erogata: " + e.getMessage(), e);
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

        Prescription prescription = new Prescription(doctor, patient, drug, dosage, frequency, issueDate != null ? issueDate.toLocalDate() : java.time.LocalDate.now(java.time.ZoneId.systemDefault()));
        prescription.setId(rs.getInt("id"));
        return prescription;
    }
}