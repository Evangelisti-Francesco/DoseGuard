package it.ispwproject.doseguard.dao.memory;

import it.ispwproject.doseguard.dao.SpecializationDAO;
import it.ispwproject.doseguard.demo.DemoDataStore;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Specialization;

import java.util.List;

public class SpecializationDAOMemory implements SpecializationDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public List<Specialization> getAllSpecializations() throws DAOException{
        return List.copyOf(store.getSpecializations());
    }

    @Override
    public Specialization findById(int id) throws DAOException {
        return store.getSpecializations().stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElse(null);
    }

}
