package it.ispwproject.doseguard.dao;

import it.ispwproject.doseguard.dao.db.*;
import it.ispwproject.doseguard.dao.file.BookingDAOFile;
import it.ispwproject.doseguard.dao.file.TimeSlotDAOFile;
import it.ispwproject.doseguard.dao.memory.*;

public class DAOFactory {

    public static final String DATABASE = "database";
    public static final String FILE = "file";
    public static final String MEMORY = "memory";

    // Impostato di default su DATABASE per lo sviluppo iniziale
    private static String persistence = DATABASE;

    private DAOFactory() {}

    public static void setPersistence(String mode) {
        if(mode != null && !mode.isBlank()) {
            persistence = mode;
        }
    }

    public static String getPersistence() {return persistence;}

    public static LoginDAO getLoginDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new LoginDAOMemory();
        return new LoginDAODB();
    }

    public static UserDAO getUserDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new UserDAOMemory();
        return new UserDAODB();
    }

    public static PatientDAO getPatientDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new PatientDAOMemory();
        return new PatientDAODB();
    }

    public static DoctorDAO getDoctorDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new DoctorDAOMemory();
        return new DoctorDAODB();
    }

    public static ProgressDAO getProgressDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new ProgressDAOMemory();
        return new ProgressDAODB();
    }

    public static SpecializationDAO getSpecializationDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new SpecializationDAOMemory();
        return new SpecializationDAODB();
    }


    public static BookingDAO getBookingDAO() {
        return switch (persistence.toLowerCase()) {
            case FILE   -> new BookingDAOFile();
            case MEMORY -> new BookingDAOMemory();
            default     -> new BookingDAODB();
        };
    }

    public static TimeSlotDAO getTimeSlotDAO() {
        return switch (persistence.toLowerCase()) {
            case FILE   -> new TimeSlotDAOFile();
            case MEMORY -> new TimeSlotDAOMemory();
            default     -> new TimeSlotDAODB();
        };
    }


    public static RegistrationDAO getRegistrationDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new RegistrationDAOMemory();
        return new RegistrationDAODB();
    }

    public static PrescriptionDAO getPrescriptionDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new PrescriptionDAOMemory();
        return new PrescriptionDAODB();
    }

    public static MedicationDAO getMedicationDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new MedicationDAOMemory();
        return new MedicationDAODB();
    }

}
