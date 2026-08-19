package it.ispwproject.doseguard.dao.file;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.ispwproject.doseguard.dao.AbstractBookingDAO;
import it.ispwproject.doseguard.enumerator.AppointmentStatus;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Booking;
import it.ispwproject.doseguard.model.Patient;
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

public class BookingDAOFile extends AbstractBookingDAO {

    private static final String FILE_PATH = "bookings.json";
    private static final String SLOTS_FILE_PATH = "timeslots.json";
    private final Gson gson;

    public BookingDAOFile() {
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
                    public boolean shouldSkipClass(Class<?> clazz) { return false; }
                })
                .addDeserializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getName().equals("observers");
                    }
                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) { return false; }
                })
                .setPrettyPrinting()
                .create();

        loadAllFromFile().forEach(this::addToCache);
    }

    @Override
    public void save(Booking booking) throws DAOException {
        booking.setId(generateId());
        booking.setStatus(AppointmentStatus.CONFIRMED);
        addToCache(booking);
        saveToFile();
        if (booking.getTimeSlot() != null) {
            markSlotAsBooked(booking.getTimeSlot().getId());
        }
    }

    @Override
    public List<Booking> findByPatient(int patientId) throws DAOException {
        List<Booking> cached = findInCacheByPatient(patientId);
        if (!cached.isEmpty()) return cached;
        loadAllFromFile().forEach(this::addToCache);
        return findInCacheByPatient(patientId);
    }

    @Override
    public List<Booking> findByDoctor(int doctorId) throws DAOException {
        return identityMap.stream()
                .filter(b -> b.getDoctor() != null && b.getDoctor().getId() == doctorId
                        && b.getStatus() == AppointmentStatus.CONFIRMED)
                .toList();
    }

    @Override
    public List<Booking> findAll() throws DAOException {
        return new ArrayList<>(identityMap);
    }

    @Override
    public List<Booking> findCompletedBookings(int patientId, int doctorId) throws DAOException {
        return identityMap.stream()
                .filter(b -> b.getPatient() != null && b.getPatient().getId() == patientId
                        && b.getDoctor() != null && b.getDoctor().getId() == doctorId
                        && b.getStatus() == AppointmentStatus.CONFIRMED
                        && b.getTimeSlot() != null
                        && !b.getTimeSlot().getDate().isAfter(LocalDate.now(ZoneId.systemDefault())))
                .toList();
    }

    @Override
    public List<Booking> findUpcomingBookings(int patientId, int doctorId) throws DAOException {
        return identityMap.stream()
                .filter(b -> b.getPatient() != null && b.getPatient().getId() == patientId
                        && b.getDoctor() != null && b.getDoctor().getId() == doctorId
                        && b.getStatus() == AppointmentStatus.CONFIRMED
                        && b.getTimeSlot() != null
                        && b.getTimeSlot().getDate().isAfter(LocalDate.now(ZoneId.systemDefault())))
                .toList();
    }

    @Override
    public void cancel(int bookingId, int patientId) throws DAOException {
        Booking booking = findInCache(bookingId);
        if (booking == null) {
            throw new DAOException("Prenotazione non trovata (ID: " + bookingId + ")");
        }
        Patient patient = booking.getPatient();
        if (patient == null || patient.getId() != patientId) {
            throw new DAOException("Non puoi annullare una prenotazione che non ti appartiene.");
        }

        booking.cancel();
        updateInCache(bookingId);
        saveToFile();

        if (booking.getTimeSlot() != null) {
            markSlotAsAvailable(booking.getTimeSlot().getId());
        }
    }

    private int generateId() {
        return identityMap.stream()
                .mapToInt(Booking::getId)
                .max()
                .orElse(0) + 1;
    }

    private List<Booking> loadAllFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Booking>>() {}.getType();
            List<Booking> loaded = gson.fromJson(reader, listType);
            return loaded != null ? loaded : new ArrayList<>();
        } catch (IOException e) {
            AppLogger.logError("Errore lettura bookings file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveToFile() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(identityMap, writer);
        } catch (IOException e) {
            AppLogger.logError("Errore salvataggio bookings su file: " + e.getMessage());
        }
    }

    private void markSlotAsBooked(int slotId) {
        updateSlotAvailability(slotId, false);
    }

    private void markSlotAsAvailable(int slotId) {
        updateSlotAvailability(slotId, true);
    }

    private void updateSlotAvailability(int slotId, boolean available) {
        File file = new File(SLOTS_FILE_PATH);
        if (!file.exists()) return;
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<TimeSlot>>() {}.getType();
            List<TimeSlot> slots = gson.fromJson(reader, listType);
            if (slots == null) return;
            slots.stream()
                    .filter(s -> s.getId() == slotId)
                    .findFirst()
                    .ifPresent(s -> {
                        s.setAvailable(available);
                        s.setReservedUntil(null);
                    });
            try (Writer writer = new FileWriter(file)) {
                gson.toJson(slots, writer);
            }
        } catch (IOException e) {
            AppLogger.logError("Errore aggiornamento slot disponibilità: " + e.getMessage());
        }
    }
}
