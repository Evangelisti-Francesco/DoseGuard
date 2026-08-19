package it.ispwproject.doseguard.dao.memory;

import it.ispwproject.doseguard.dao.TimeSlotDAO;
import it.ispwproject.doseguard.demo.DemoDataStore;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Doctor;
import it.ispwproject.doseguard.model.TimeSlot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

public class TimeSlotDAOMemory implements TimeSlotDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public List<TimeSlot> getAvailableByDoctor(Doctor doctor) throws DAOException {
        return store.getTimeSlots().stream()
                .filter(s -> s.getDoctor() != null
                        && s.getDoctor().getId() == doctor.getId()
                        && s.isAvailable()
                        && !s.getDate().isBefore(LocalDate.now(ZoneId.systemDefault())))
                .toList();
    }

    @Override
    public List<TimeSlot> getAllByDoctor(int doctorId) throws DAOException {
        return store.getTimeSlots().stream()
                .filter(s -> s.getDoctor() != null
                        && s.getDoctor().getId() == doctorId
                        && (s.getDate().isAfter(LocalDate.now(ZoneId.systemDefault())) ||
                        (s.getDate().isEqual(LocalDate.now(ZoneId.systemDefault())) && s.getStartTime().isAfter(LocalTime.now(ZoneId.systemDefault())))))
                .toList();
    }

    @Override
    public TimeSlot findById(int id) throws DAOException {
        return store.getTimeSlots().stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void save(TimeSlot slot, int doctorId) throws DAOException {
        slot.setId(store.nextSlotId());
        store.getUsers().stream()
                .filter(u -> u instanceof Doctor && u.getId() == doctorId)
                .map(u -> (Doctor) u)
                .findFirst()
                .ifPresent(slot::setDoctor);
        store.getTimeSlots().add(slot);
    }

    @Override
    public boolean reserveSlot(int slotId, int minutes) throws DAOException {
        synchronized (store.getTimeSlots()) {
            TimeSlot slot = store.getTimeSlots().stream()
                    .filter(s -> s.getId() == slotId && s.isAvailable()
                            && (s.getReservedUntil() == null ||
                            s.getReservedUntil().isBefore(LocalDateTime.now(ZoneId.systemDefault()))))
                    .findFirst()
                    .orElse(null);
            if (slot == null) return false;
            slot.setReservedUntil(LocalDateTime.now(ZoneId.systemDefault()).plusMinutes(minutes));
            return true;
        }
    }

    @Override
    public void releaseSlot(int slotId) throws DAOException {
        store.getTimeSlots().stream()
                .filter(s -> s.getId() == slotId)
                .findFirst()
                .ifPresent(s -> s.setReservedUntil(null));
    }

    @Override
    public void delete(int slotId, int doctorId) throws DAOException {
        boolean removed = store.getTimeSlots().removeIf(s ->
                s.getId() == slotId
                        && s.getDoctor() != null
                        && s.getDoctor().getId() == doctorId
                        && s.isAvailable());
        if (!removed) throw new DAOException("Slot non trovato o già prenotato.");
    }
}
