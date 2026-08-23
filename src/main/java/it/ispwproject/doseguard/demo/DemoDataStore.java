package it.ispwproject.doseguard.demo;

import it.ispwproject.doseguard.enumerator.AppointmentStatus;
import it.ispwproject.doseguard.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fonte dati condivisa per la modalità demo (in-memory).
 * Singleton — una sola istanza per tutta l'applicazione.
 * Tutti i DAO Memory leggono e scrivono su questa classe.
 */
public class DemoDataStore {

    private static DemoDataStore instance;

    private final List<User>           users           = new ArrayList<>();
    private final List<Specialization> specializations = new ArrayList<>();
    private final List<TimeSlot>       timeSlots       = new ArrayList<>();
    private final List<Booking>        bookings        = new ArrayList<>();
    private final List<PatientProgress> progresses = new ArrayList<>();

    private final Map<Integer, List<Integer>> favouritesByPatient   = new HashMap<>();
    private final Map<Integer, List<Integer>> specializationsByDoctor = new HashMap<>();

    private int nextUserId    = 10;
    private int nextBookingId = 3;
    private int nextProgressId = 3;
    private int nextSlotId    = 10;

    private DemoDataStore() {
        initData();
    }

    public static synchronized DemoDataStore getInstance() {
        if (instance == null) {
            instance = new DemoDataStore();
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }

    private void initData() {

        // ── Specializzazioni ──────────────────────────────────────
        Specialization spec1 = new Specialization(1, "Cardiologia");
        Specialization spec2 = new Specialization(2, "Dermatologia");
        Specialization spec3 = new Specialization(3, "Pediatria");
        Specialization spec4 = new Specialization(4, "Medicina Generale");

        specializations.add(spec1);
        specializations.add(spec2);
        specializations.add(spec3);
        specializations.add(spec4);

        // ── Utenti ───────────────────────────────────────────────
        Patient p1 = new Patient(1, "Mario", "Rossi", "patient@demo", null,"RSSMRA80A01H501Z");
        Patient p2 = new Patient(2, "Emma", "Verdi", "emma@demo", null,"VRDEMM92M41H501X");

        Doctor d1 = new Doctor(3, "Dr. Roberto", "Bianchi", "doctor@demo", null,
                "Specialista in Cardiologia con 10 anni di esperienza.");
        Doctor d2 = new Doctor(4, "Dr.ssa Laura", "Neri", "laura@demo", null,
                "Dermatologa ed esperta in medicina estetica.");
        Pharmacist ph1 = new Pharmacist(5,"Massimiliano","Gialli","pharmacist@demo",null,"Farmacia Centrale");

        users.add(p1);
        users.add(p2);
        users.add(d1);
        users.add(d2);
        users.add(ph1);

        // Mappatura Medico-Specializzazione
        specializationsByDoctor.put(3, new ArrayList<>(List.of(1, 4))); // Cardiologia, Medicina Generale
        specializationsByDoctor.put(4, new ArrayList<>(List.of(2)));    // Dermatologia

        // ── Slot Orari ───────────────────────────────────────────
        TimeSlot slot1 = new TimeSlot(1, d1, LocalDate.now(ZoneId.systemDefault()).plusDays(1),
                LocalTime.of(9, 0));
        TimeSlot slot2 = new TimeSlot(2, d1, LocalDate.now(ZoneId.systemDefault()).plusDays(1),
                LocalTime.of(10, 0));
        TimeSlot slot3 = new TimeSlot(3, d1, LocalDate.now(ZoneId.systemDefault()).plusDays(2),
                LocalTime.of(15, 0));
        TimeSlot slot4 = new TimeSlot(4, d2, LocalDate.now(ZoneId.systemDefault()).plusDays(1),
                LocalTime.of(11, 0));

        timeSlots.add(slot1);
        timeSlots.add(slot2);
        timeSlots.add(slot3);
        timeSlots.add(slot4);

        // ── Prenotazioni ─────────────────────────────────────────
        Booking b1 = new Booking(p1, d1, spec1, slot1, "Visita cardiologica di controllo");
        b1.setStatus(AppointmentStatus.CONFIRMED);
        slot1.setAvailable(false);
        bookings.add(b1);

        Booking b2 = new Booking(p2, d2, spec2, slot4, "Mappatura nei");
        b2.setStatus(AppointmentStatus.CONFIRMED);
        slot4.setAvailable(false);
        bookings.add(b2);
    }

    // Getters per le liste di dati
    public List<User>           getUsers()                  { return users; }
    public List<Specialization> getSpecializations()        { return specializations; }
    public List<TimeSlot>       getTimeSlots()             { return timeSlots; }
    public List<Booking>        getBookings()              { return bookings; }
    public List<PatientProgress> getProgresses() { return progresses; }
    public Map<Integer, List<Integer>> getFavouritesByPatient()      { return favouritesByPatient; }
    public Map<Integer, List<Integer>> getSpecializationsByDoctor() { return specializationsByDoctor; }

    // Generatori di ID autoincrementanti
    public int nextUserId()    { return nextUserId++; }
    public int nextBookingId() { return nextBookingId++; }
    public int nextProgressId() { return nextProgressId++; }
    public int nextSlotId()    { return nextSlotId++; }
}