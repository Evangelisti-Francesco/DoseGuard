package it.ispwproject.doseguard.dao.memory;

import it.ispwproject.doseguard.dao.DoctorDAO;
import it.ispwproject.doseguard.demo.DemoDataStore;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Doctor;
import it.ispwproject.doseguard.model.Specialization;

import java.util.List;
import java.util.Map;

public class DoctorDAOMemory implements DoctorDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public List<Doctor> getBySpecialization(Specialization specialization) throws DAOException {
        if (specialization == null) return List.of();

        List<Integer> doctorIds = store.getSpecializationsByDoctor().entrySet().stream()
                .filter(e -> e.getValue().contains(specialization.getId()))
                .map(Map.Entry::getKey)
                .toList();

        return store.getUsers().stream()
                .filter(Doctor.class::isInstance)
                .map(Doctor.class::cast)
                .filter(d -> doctorIds.contains(d.getId()))
                .toList();
    }

    @Override
    public List<Doctor> getAllDoctors() throws DAOException {
        return store.getUsers().stream()
                .filter(u -> u instanceof Doctor)
                .map(u -> (Doctor) u)
                .toList();
    }

    @Override
    public Doctor findById(int id) throws DAOException {
        return store.getUsers().stream()
                .filter(Doctor.class::isInstance)
                .map(Doctor.class::cast)
                .filter(d -> d.getId() == id)
                .findFirst()
                .orElse(null);
    }


}
