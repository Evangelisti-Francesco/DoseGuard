package it.ispwproject.doseguard.controller.applicativo;

import it.ispwproject.doseguard.bean.TimeSlotBean;
import it.ispwproject.doseguard.dao.BookingDAO;
import it.ispwproject.doseguard.dao.DAOFactory;
import it.ispwproject.doseguard.dao.TimeSlotDAO;
import it.ispwproject.doseguard.exception.AvailabilityException;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Booking;
import it.ispwproject.doseguard.model.Doctor;
import it.ispwproject.doseguard.model.TimeSlot;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AvailabilityController {

    private final TimeSlotDAO timeSlotDAO;
    private final BookingDAO bookingDAO;

    public AvailabilityController() {
        this.timeSlotDAO = DAOFactory.getTimeSlotDAO();
        this.bookingDAO  = DAOFactory.getBookingDAO();
    }

    public void addSlot(TimeSlotBean slotBean) throws DAOException, AvailabilityException {
        if (slotBean.getDate().isBefore(LocalDate.now(ZoneId.systemDefault()))) {
            throw new AvailabilityException("Non puoi aggiungere slot nel passato.");
        }

        Doctor doctor = (Doctor) SessionManager.getInstance().getLoggedUser();
        TimeSlot newSlot = new TimeSlot(0, doctor, slotBean.getDate(),
                slotBean.getStartTime());

        for (TimeSlot s : timeSlotDAO.getAvailableByDoctor(doctor)) {
            if (newSlot.overlaps(s)) {
                throw new AvailabilityException(
                        "Lo slot si sovrappone con uno già esistente: " +
                                s.getDate() + " " + s.getStartTime());
            }
        }

        timeSlotDAO.save(newSlot, doctor.getId());
        slotBean.setId(newSlot.getId());
    }

    public List<TimeSlotBean> getSlots() throws DAOException {
        Doctor doctor = (Doctor) SessionManager.getInstance().getLoggedUser();
        return buildSlotBeans(timeSlotDAO.getAllByDoctor(doctor.getId()), doctor);
    }

    public List<TimeSlotBean> getPastSlots() throws DAOException {
        Doctor doctor = (Doctor) SessionManager.getInstance().getLoggedUser();
        return buildSlotBeans(timeSlotDAO.getPastByDoctor(doctor.getId()), doctor);
    }

    public Map<Integer, String> getSpecializationBySlot() throws DAOException {
        Doctor doctor = (Doctor) SessionManager.getInstance().getLoggedUser();
        Map<Integer, String> result = new HashMap<>();
        for (Booking b : bookingDAO.findByDoctor(doctor.getId())) {
            if (b.getTimeSlot() != null && b.getSpecialization() != null) {
                result.put(b.getTimeSlot().getId(), b.getSpecialization().getSpecialization());
            }
        }
        return result;
    }

    public void deleteSlot(int slotId) throws DAOException {
        Doctor doctor = (Doctor) SessionManager.getInstance().getLoggedUser();
        timeSlotDAO.delete(slotId, doctor.getId());
    }

    private List<TimeSlotBean> buildSlotBeans(List<TimeSlot> slots, Doctor doctor) throws DAOException {
        Map<Integer, Booking> bookingBySlot = new HashMap<>();
        for (Booking b : bookingDAO.findByDoctor(doctor.getId())) {
            if (b.getTimeSlot() != null) {
                bookingBySlot.put(b.getTimeSlot().getId(), b);
            }
        }

        List<TimeSlotBean> result = new ArrayList<>();
        for (TimeSlot slot : slots) {
            TimeSlotBean bean = new TimeSlotBean(slot.getId(), slot.getDate(),
                    slot.getStartTime(), slot.getEndTime(), slot.isAvailable());
            Booking booking = bookingBySlot.get(slot.getId());
            if (booking != null) {
                if (booking.getPatient() != null)
                    bean.setBookedByName(booking.getPatient().getFullName());
                bean.setMeetLink(booking.getMeetLink());
            }
            result.add(bean);
        }
        return result;
    }
}
