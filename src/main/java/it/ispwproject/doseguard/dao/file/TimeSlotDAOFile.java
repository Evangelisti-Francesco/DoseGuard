package it.ispwproject.doseguard.dao.file;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.ispwproject.doseguard.dao.TimeSlotDAO;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Doctor;
import it.ispwproject.doseguard.model.TimeSlot;
import it.ispwproject.doseguard.util.logger.AppLogger;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotDAOFile implements TimeSlotDAO {

    private static final String FILE_PATH = "timeslots.json";
    private final Gson gson;
    private final List<TimeSlot> cache;

    public TimeSlotDAOFile() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .addSerializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getName().equals("observers");
                    }
                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .addDeserializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getName().equals("observers");
                    }
                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .setPrettyPrinting()
                .create();
        this.cache = loadFromFile();
    }

    @Override
    public List<TimeSlot> getAvailableByDoctor(Doctor doctor) throws DAOException {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        LocalTime now = LocalTime.now(ZoneId.systemDefault());

        return cache.stream()
                .filter(s -> s.getDoctor() != null
                        && s.getDoctor().getId() == doctor.getId()
                        && s.isAvailable()
                        && (s.getDate().isAfter(today) ||
                        (s.getDate().isEqual(today) && s.getStartTime().isAfter(now))))
                .toList();
    }

    @Override
    public List<TimeSlot> getAllByDoctor(int doctorId) throws DAOException {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        LocalTime now = LocalTime.now(ZoneId.systemDefault());

        return cache.stream()
                .filter(s -> s.getDoctor() != null
                        && s.getDoctor().getId() == doctorId
                        && (s.getDate().isAfter(today) ||
                        (s.getDate().isEqual(today) && s.getStartTime().isAfter(now))))
                .toList();
    }

    @Override
    public List<TimeSlot> getPastByDoctor(int doctorId) throws DAOException {
        return cache.stream()
                .filter(s -> s.getDoctor() != null
                        && s.getDoctor().getId() == doctorId
                        && (s.getDate().isBefore(LocalDate.now(ZoneId.systemDefault())) ||
                        (s.getDate().isEqual(LocalDate.now(ZoneId.systemDefault()))
                                && s.getStartTime().isBefore(LocalTime.now(ZoneId.systemDefault())))))
                .toList();
    }


    @Override
    public TimeSlot findById(int id) throws DAOException {
        return cache.stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void save(TimeSlot slot, int doctorId) throws DAOException {
        slot.setId(generateId());
        cache.add(slot);
        saveToFile();
    }

    private int generateId() {
        return cache.stream()
                .mapToInt(TimeSlot::getId)
                .max()
                .orElse(0) + 1;
    }

    private List<TimeSlot> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<TimeSlot>>() {}.getType();
            List<TimeSlot> loaded = gson.fromJson(reader, listType);
            return loaded != null ? loaded : new ArrayList<>();
        } catch (IOException e) {
            AppLogger.logError("Errore nella lettura del file " + FILE_PATH + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveToFile() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(cache, writer);
        } catch (IOException e) {
            AppLogger.logError("Errore salvataggio timeslots su file: " + e.getMessage());
        }
    }

    @Override
    public boolean reserveSlot(int slotId, int minutes) throws DAOException {
        synchronized (cache) {
            LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
            TimeSlot slot = cache.stream()
                    .filter(s -> s.getId() == slotId && s.isAvailable()
                            && (s.getReservedUntil() == null || s.getReservedUntil().isBefore(now)))
                    .findFirst()
                    .orElse(null);
            if (slot == null) return false;
            slot.setReservedUntil(now.plusMinutes(minutes));
            saveToFile();
            return true;
        }
    }

    @Override
    public void releaseSlot(int slotId) throws DAOException {
        cache.stream()
                .filter(s -> s.getId() == slotId)
                .findFirst()
                .ifPresent(s -> {
                    s.setReservedUntil(null);
                    saveToFile();
                });
    }

    @Override
    public void delete(int slotId, int doctorId) throws DAOException {
        boolean removed = cache.removeIf(s ->
                s.getId() == slotId
                        && s.getDoctor() != null
                        && s.getDoctor().getId() == doctorId
                        && s.isAvailable());
        if (!removed) {
            throw new DAOException("Slot non trovato o non eliminabile (potrebbe essere già prenotato).");
        }
        saveToFile();
    }
}
