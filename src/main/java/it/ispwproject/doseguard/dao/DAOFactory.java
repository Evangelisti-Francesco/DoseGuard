package it.ispwproject.doseguard.dao;

public class DAOFactory {

    public static final String DATABASE = "database";
    public static final String FILE = "file";
    public static final String MEMORY = "memory";

    // Impostato di default su MEMORY per lo sviluppo iniziale
    private static String persistence = MEMORY;

    private DAOFactory() {}

    public static void setPersistence(String mode) {
        if(mode != null && !mode.isBlank()) {
            persistence = mode;
        }
    }

    public static String getPersistence() {return persistence;}

    /*
    public static LoginDAO getLoginDAO() {
        if(MEMORY.equalsIgnoreCase(persistence)) return new LoginDAOMemory();
        return new LoginDAODB();
    }

    public static UserDAO getUserDAO() {
        return switch (persistence.toLowerCase()) {
            case FILE   -> new UserDAOFile();
            case MEMORY -> new UserDAOMemory();
            default     -> new UserDAODB();
        };
    }

    public static PatientDAO getPatientDAO() {
        return switch (persistence.toLowerCase()) {
            case FILE   -> new PatientDAOFile();
            case MEMORY -> new PatientDAOMemory();
            default     -> new PatientDAODB();
        };
    }

    public static DoctorDAO getDoctorDAO() {
        return switch (persistence.toLowerCase()) {
            case FILE   -> new DoctorDAOFile();
            case MEMORY -> new DoctorDAOMemory();
            default     -> new DoctorDAODB();
        };
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
        return switch (persistence.toLowerCase()) {
            case FILE   -> new RegistrationDAOFile();
            case MEMORY -> new RegistrationDAOMemory();
            default     -> new RegistrationDAODB();
        };
    }
    */
}
