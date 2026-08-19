package it.ispwproject.doseguard.dao.memory;

import it.ispwproject.doseguard.dao.PatientDAO;
import it.ispwproject.doseguard.demo.DemoDataStore;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PatientDAOMemory implements PatientDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();
    private final Map<Integer, List<Integer>> favouritesByPatient = DemoDataStore.getInstance().getFavouritesByPatient();

    @Override
    public Patient findById(int id) throws DAOException {
        return store.getUsers().stream()
                .filter(u -> u instanceof Patient && u.getId() == id)
                .map(u -> (Patient) u)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Patient> getByDoctor(int doctorId) throws DAOException {
        List<Integer> patientIds = store.getBookings().stream()
                .filter(b -> b.getDoctor() != null && b.getDoctor().getId() == doctorId)
                .map(b -> b.getPatient().getId())
                .distinct()
                .toList();

        return store.getUsers().stream()
                .filter(u -> u instanceof Patient && patientIds.contains(u.getId()))
                .map(u -> (Patient) u)
                .toList();
    }

    @Override
    public void addFavouriteDoctor(int patientId, int doctorId) throws DAOException {
        favouritesByPatient
                .computeIfAbsent(patientId, id -> new ArrayList<>());

        if (!favouritesByPatient.get(patientId).contains(doctorId)) {
            favouritesByPatient.get(patientId).add(doctorId);
        }
    }

    @Override
    public void removeFavouriteDoctor(int patientId, int doctorId) throws DAOException {
        List<Integer> favourites = favouritesByPatient.get(patientId);

        if (favourites != null) {
            favourites.remove(Integer.valueOf(doctorId));
        }
    }

    @Override
    public boolean isFavouriteDoctor(int patientId, int doctorId) throws DAOException {
        List<Integer> favourites = favouritesByPatient.get(patientId);

        return favourites != null && favourites.contains(doctorId);
    }

}
