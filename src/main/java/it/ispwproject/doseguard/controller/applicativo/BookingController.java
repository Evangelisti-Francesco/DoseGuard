package it.ispwproject.doseguard.controller.applicativo;

import it.ispwproject.doseguard.bean.*;
import it.ispwproject.doseguard.dao.*;
import it.ispwproject.doseguard.enumerator.AppointmentStatus;
import it.ispwproject.doseguard.exception.BookingException;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.*;
import it.ispwproject.doseguard.pattern.observer.BookingCancellationObserver;
import it.ispwproject.doseguard.pattern.observer.BookingConfirmationObserver;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class BookingController {

    private final BookingDAO bookingDAO;
    private final SpecializationDAO specializationDAO;
    private final DoctorDAO doctorDAO;
    private final TimeSlotDAO timeSlotDAO;
    private final PatientDAO patientDAO;

    private static final int RESERVATION_MINUTES = 3;

    public BookingController() {
        this.bookingDAO = DAOFactory.getBookingDAO();
        this.specializationDAO = DAOFactory.getSpecializationDAO();
        this.doctorDAO = DAOFactory.getDoctorDAO();
        this.timeSlotDAO = DAOFactory.getTimeSlotDAO();
        this.patientDAO = DAOFactory.getPatientDAO();
    }

    public List<SpecializationBean> getAvailableSpecializations() throws DAOException {
        List<SpecializationBean> result = new ArrayList<>();
        for (Specialization spec : specializationDAO.getAllSpecializations()) {
            result.add(new SpecializationBean(spec.getId(), spec.getSpecialization()));
        }
        return result;
    }

    public List<DoctorBean> getDoctorsBySpecialization(SpecializationBean specBean) throws DAOException {
        Specialization spec = new Specialization(specBean.getId(), specBean.getName());
        List<DoctorBean> result = new ArrayList<>();

        User user = SessionManager.getInstance().getLoggedUser();
        if (!(user instanceof Patient patient)) {
            throw new DAOException("L'utente collegato non è un paziente.");
        }

        for (Doctor doctor : doctorDAO.getBySpecialization(spec)) {
            boolean favourite = patientDAO.isFavouriteDoctor(patient.getId(), doctor.getId());
            result.add(new DoctorBean(doctor.getId(), doctor.getName(),
                    doctor.getSurname(), doctor.getEmail(), favourite));
        }

        return result;
    }

    public List<TimeSlotBean> getDoctorAvailability(DoctorBean doctorBean) throws DAOException {
        Doctor doctor = new Doctor(doctorBean.getId(), doctorBean.getName(),
                doctorBean.getSurname(), null,null, null);
        List<TimeSlotBean> result = new ArrayList<>();

        for (TimeSlot slot : timeSlotDAO.getAvailableByDoctor(doctor)) {
            result.add(new TimeSlotBean(slot.getId(), slot.getDate(),
                    slot.getStartTime(), slot.isAvailable()));
        }

        return result;
    }


    public AppointmentResponseBean prepareBookingSummary(AppointmentRequestBean request)
            throws DAOException, BookingException {
        Doctor doctor = doctorDAO.findById(request.getDoctor().getId());
        Specialization spec = specializationDAO.findById(request.getSpecialization().getId());
        TimeSlot slot = timeSlotDAO.findById(request.getSlot().getId());

        if (doctor == null) throw new DAOException("Medico non trovato.");
        if (spec == null) throw new DAOException("Specializzazione non trovata.");
        if (slot == null) throw new DAOException("Slot orario non trovato.");

        boolean reserved = timeSlotDAO.reserveSlot(slot.getId(), RESERVATION_MINUTES);
        if (!reserved) throw new BookingException(
                "Lo slot è stato appena prenotato da un altro paziente. Seleziona un altro orario.");

        return new AppointmentResponseBean(0, request.getPatient(),
                new DoctorBean(doctor.getId(), doctor.getName(), doctor.getSurname(), doctor.getEmail(), false),
                new SpecializationBean(spec.getId(), spec.getSpecialization()),
                new TimeSlotBean(slot.getId(), slot.getDate(), slot.getStartTime(),slot.isAvailable()),
                AppointmentStatus.PENDING, request.getNotes());
    }

    public void releaseSlot(int slotId) throws DAOException {
        timeSlotDAO.releaseSlot(slotId);
    }

    public AppointmentResponseBean createBooking(AppointmentRequestBean request)
            throws DAOException, BookingException {

        Patient patient = (Patient) SessionManager.getInstance().getLoggedUser();
        Doctor doctor = doctorDAO.findById(request.getDoctor().getId());
        Specialization spec = specializationDAO.findById(request.getSpecialization().getId());
        TimeSlot slot = timeSlotDAO.findById(request.getSlot().getId());

        if (doctor == null) throw new DAOException("Medico non trovato.");
        if (spec == null) throw new DAOException("Specializzazione non trovata.");
        if (slot == null) throw new DAOException("Slot orario non trovato.");

        for (Booking b : bookingDAO.findByPatient(patient.getId())) {
            if (b.getStatus() == AppointmentStatus.CANCELLED) continue;
            TimeSlot existing = b.getTimeSlot();
            if (existing != null && slot.overlaps(existing)) {
                throw new BookingException(
                        "Hai già un'altra visita prenotata in questo slot orario: " +
                                existing.getDate() + " alle ore " + existing.getStartTime());
            }
        }

        Booking booking = new Booking(patient, doctor, spec, slot, request.getNotes());
        booking.attach(new BookingConfirmationObserver(booking));
        booking.confirm();
        bookingDAO.save(booking);

        return new AppointmentResponseBean(
                booking.getId(),
                new PatientBean(patient.getId(), patient.getName(), patient.getSurname(), patient.getEmail(), patient.getFiscalCode()),
                new DoctorBean(doctor.getId(), doctor.getName(), doctor.getSurname(), doctor.getEmail(), false),
                new SpecializationBean(spec.getId(), spec.getSpecialization()),
                new TimeSlotBean(slot.getId(), slot.getDate(), slot.getStartTime(), slot.isAvailable()),
                booking.getStatus(), booking.getNotes());
    }

    public List<AppointmentResponseBean> getPatientBookings(int patientId) throws DAOException {
        List<AppointmentResponseBean> result = new ArrayList<>();
        for (Booking booking : bookingDAO.findByPatient(patientId)) {
            Doctor doctor = booking.getDoctor();
            Specialization spec = booking.getSpecialization();
            TimeSlot slot = booking.getTimeSlot();
            if (doctor == null || spec == null || slot == null) continue;

            result.add(new AppointmentResponseBean(
                    booking.getId(),
                    new PatientBean(booking.getPatient().getId(), booking.getPatient().getName(), booking.getPatient().getSurname(), booking.getPatient().getEmail(), booking.getPatient().getFiscalCode()),
                    new DoctorBean(doctor.getId(), doctor.getName(), doctor.getSurname(), doctor.getEmail(), false),
                    new SpecializationBean(spec.getId(), spec.getSpecialization()),
                    new TimeSlotBean(slot.getId(), slot.getDate(), slot.getStartTime(), slot.isAvailable()),
                    booking.getStatus(), booking.getNotes()));
        }
        return result;
    }

    public void cancelBooking(int bookingId, int patientId) throws DAOException {
        List<Booking> bookings = bookingDAO.findByPatient(patientId);
        Booking booking = bookings.stream()
                .filter(b -> b.getId() == bookingId)
                .findFirst()
                .orElse(null);

        if (booking != null) {
            booking.attach(new BookingCancellationObserver(booking));
        }

        bookingDAO.cancel(bookingId, patientId);
    }

    public void addDoctorToFavourites(int doctorId) throws DAOException {
        Patient patient = (Patient) SessionManager.getInstance().getLoggedUser();
        Doctor doctor = doctorDAO.findById(doctorId);
        if (doctor == null) throw new DAOException("Medico non trovato.");
        patient.addFavouriteDoctor(doctor);
        patientDAO.addFavouriteDoctor(patient.getId(), doctorId);
    }

    public void removeDoctorFromFavourites(int doctorId) throws DAOException {
        Patient patient = (Patient) SessionManager.getInstance().getLoggedUser();
        Doctor doctor = doctorDAO.findById(doctorId);
        if (doctor == null) throw new DAOException("Medico non trovato.");
        patient.removeFavouriteDoctor(doctorId);
        patientDAO.removeFavouriteDoctor(patient.getId(), doctorId);
    }
}
