package it.ispwproject.doseguard.dao.memory;

import it.ispwproject.doseguard.demo.DemoDataStore;
import it.ispwproject.doseguard.dao.ProgressDAO;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.PatientProgress;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class ProgressDAOMemory implements ProgressDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public void saveOrUpdate(PatientProgress progress) throws DAOException {
        PatientProgress existing = findByPatientAndDoctor(
                progress.getDoctor().getId(), progress.getPatient().getId());
        if (existing == null) {
            progress.setId(store.nextProgressId());
            progress.setUpdatedAt(LocalDateTime.now(ZoneId.systemDefault()));
            store.getProgresses().add(progress);
        } else {
            existing.setNotes(progress.getNotes());
            existing.setUpdatedAt(LocalDateTime.now(ZoneId.systemDefault()));
        }
    }

    @Override
    public PatientProgress findByPatientAndDoctor(int doctorId, int patientId) throws DAOException {
        return store.getProgresses().stream()
                .filter(p -> p.getDoctor() != null && p.getDoctor().getId() == doctorId
                        && p.getPatient() != null && p.getPatient().getId() == patientId)
                .findFirst()
                .orElse(null);
    }
}