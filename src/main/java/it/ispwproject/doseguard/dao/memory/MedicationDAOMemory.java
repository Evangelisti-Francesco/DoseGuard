package it.ispwproject.doseguard.dao.memory;

import it.ispwproject.doseguard.dao.MedicationDAO;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Medication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MedicationDAOMemory implements MedicationDAO {

    private static final Map<Integer, Medication> memoryMap = new ConcurrentHashMap<>();
    private static final AtomicInteger idCounter = new AtomicInteger(1);

    @Override
    public List<Medication> getByPatient(int patientId) throws DAOException {
        List<Medication> result = new ArrayList<>();
        for (Medication m : memoryMap.values()) {
            if (m.getPatient() != null && m.getPatient().getId() == patientId) {
                result.add(m);
            }
        }
        return result;
    }

    @Override
    public synchronized void save(Medication medication) throws DAOException {
        if (medication.getId() == 0) {
            medication.setId(idCounter.getAndIncrement());
        }
        memoryMap.put(medication.getId(), medication);
    }

    @Override
    public synchronized void markAsTaken(int medicationId, int patientId) throws DAOException {
        Medication m = memoryMap.get(medicationId);
        if (m != null && m.getPatient() != null && m.getPatient().getId() == patientId) {
            m.markAsTaken();
        } else {
            throw new DAOException("Farmaco non trovato o non autorizzato.");
        }
    }

    @Override
    public synchronized void delete(int medicationId, int patientId) throws DAOException {
        Medication m = memoryMap.get(medicationId);
        if (m != null && m.getPatient() != null && m.getPatient().getId() == patientId) {
            memoryMap.remove(medicationId);
        } else {
            throw new DAOException("Farmaco non trovato o non autorizzato.");
        }
    }
}
