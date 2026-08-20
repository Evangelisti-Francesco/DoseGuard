package it.ispwproject.doseguard.dao.memory;

import it.ispwproject.doseguard.dao.PrescriptionDAO;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Prescription;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PrescriptionDAOMemory implements PrescriptionDAO {

    private static final Map<Integer, Prescription> memoryMap = new ConcurrentHashMap<>();
    private static final AtomicInteger idCounter = new AtomicInteger(1);

    @Override
    public synchronized void save(Prescription prescription) throws DAOException {
        if (prescription.getId() == 0) {
            prescription.setId(idCounter.getAndIncrement());
        }
        memoryMap.put(prescription.getId(), prescription);
    }

    @Override
    public List<Prescription> getByPatientFiscalCode(String fiscalCode) throws DAOException {
        List<Prescription> result = new ArrayList<>();
        for (Prescription p : memoryMap.values()) {
            if (p.getPatient() != null && fiscalCode.equalsIgnoreCase(p.getPatient().getFiscalCode())) {
                result.add(p);
            }
        }
        return result;
    }

    @Override
    public Prescription findById(int prescriptionId) throws DAOException {
        return memoryMap.get(prescriptionId);
    }

    @Override
    public synchronized void markAsFulfilled(int prescriptionId, int pharmacistId) throws DAOException {
        Prescription p = memoryMap.get(prescriptionId);
        if (p != null) {
            p.setPharmacistId(pharmacistId);
        } else {
            throw new DAOException("Ricetta con ID " + prescriptionId + " non trovata.");
        }
    }
}
