package it.ispwproject.doseguard.controller.applicativo;

import it.ispwproject.doseguard.bean.*;
import it.ispwproject.doseguard.dao.*;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.*;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class PatientManagementController {

    private final PatientDAO  patientDAO;
    private final ProgressDAO progressDAO;
    private final BookingDAO  bookingDAO;

    public PatientManagementController() {
        this.patientDAO  = DAOFactory.getPatientDAO();
        this.progressDAO = DAOFactory.getProgressDAO();
        this.bookingDAO  = DAOFactory.getBookingDAO();
    }

    public List<PatientBean> getPatients() throws DAOException {
        Doctor doctor = (Doctor) SessionManager.getInstance().getLoggedUser();
        List<PatientBean> result = new ArrayList<>();
        for (Patient patient : patientDAO.getByDoctor(doctor.getId())) {
            result.add(new PatientBean(patient.getId(), patient.getName(),
                    patient.getSurname(), patient.getEmail(), patient.getFiscalCode()));
        }
        return result;
    }

    public List<TimeSlotBean> getCompletedAppointments(int patientId) throws DAOException {
        Doctor doctor = (Doctor) SessionManager.getInstance().getLoggedUser();
        return buildAppointmentResponseList(
                bookingDAO.findCompletedBookings(patientId, doctor.getId()));
    }

    public List<TimeSlotBean> getUpcomingAppointments(int patientId) throws DAOException {
        Doctor doctor = (Doctor) SessionManager.getInstance().getLoggedUser();
        return buildAppointmentResponseList(
                bookingDAO.findUpcomingBookings(patientId, doctor.getId()));
    }

    private List<TimeSlotBean> buildAppointmentResponseList(List<Booking> bookings) {
        List<TimeSlotBean> result = new ArrayList<>();
        for (Booking booking : bookings) {
            Doctor   doctor = booking.getDoctor();
            TimeSlot slot   = booking.getTimeSlot();

            if (doctor == null || slot == null) continue;

            result.add(new TimeSlotBean(
                    slot.getId(),
                    slot.getDate(),
                    slot.getStartTime(),
                    slot.isAvailable()
            ));
        }
        return result;
    }

    public void updateProgress(PatientProgressBean bean) throws DAOException {
        Doctor  doctor  = (Doctor) SessionManager.getInstance().getLoggedUser();
        Patient patient = patientDAO.findById(bean.getPatient().getId());
        if (patient == null) throw new DAOException("Paziente non trovato.");

        PatientProgress progress = progressDAO.findByPatientAndDoctor(doctor.getId(), patient.getId());
        if (progress == null) {
            progress = new PatientProgress(doctor, patient, bean.getNotes());
        } else {
            progress.updateNotes(bean.getNotes());
        }
        progressDAO.saveOrUpdate(progress);
    }

    public PatientProgressBean getProgress(int patientId) throws DAOException {
        Doctor          doctor   = (Doctor) SessionManager.getInstance().getLoggedUser();
        PatientProgress progress = progressDAO.findByPatientAndDoctor(doctor.getId(), patientId);
        if (progress == null) return null;

        PatientBean patientBean = new PatientBean(
                progress.getPatient().getId(), progress.getPatient().getName(),
                progress.getPatient().getSurname(), progress.getPatient().getEmail(),
                progress.getPatient().getFiscalCode());
        return new PatientProgressBean(patientBean, progress.getNotes(), progress.getUpdatedAt());
    }
}