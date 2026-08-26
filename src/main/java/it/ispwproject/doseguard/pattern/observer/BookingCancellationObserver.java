package it.ispwproject.doseguard.pattern.observer;

import it.ispwproject.doseguard.bean.*;
import it.ispwproject.doseguard.exception.NotificationException;
import it.ispwproject.doseguard.model.Booking;
import it.ispwproject.doseguard.service.NotificationService;
import it.ispwproject.doseguard.util.logger.AppLogger;

public class  BookingCancellationObserver implements Observer {

    private final Booking booking;

    public BookingCancellationObserver(Booking booking) {
        this.booking = booking;
    }

    @Override
    public void update() {
        try {
            AppointmentResponseBean response = buildResponse();

            NotificationService.sendBookingCancellationToPatient(
                    booking.getPatient().getEmail(),
                    response);

            NotificationService.sendBookingCancellationToDoctor(
                    booking.getDoctor().getEmail(),
                    response);

        } catch (NotificationException e) {
            AppLogger.logWarning("Notifica cancellazione non inviata: " + e.getMessage());
        }
    }

    private AppointmentResponseBean buildResponse() {
        PatientBean patientBean = new PatientBean(
                booking.getPatient().getId(),
                booking.getPatient().getName(),
                booking.getPatient().getSurname(),
                booking.getPatient().getEmail(),
                booking.getPatient().getFiscalCode());

        DoctorBean doctorBean = new DoctorBean(
                booking.getDoctor().getId(),
                booking.getDoctor().getName(),
                booking.getDoctor().getSurname(),
                booking.getDoctor().getEmail(),
                false);

        SpecializationBean specializationBean = new SpecializationBean(
                booking.getSpecialization().getId(),
                booking.getSpecialization().getSpecialization());

        TimeSlotBean slotBean = new TimeSlotBean(
                booking.getTimeSlot().getId(),
                booking.getTimeSlot().getDate(),
                booking.getTimeSlot().getStartTime(),
                booking.getTimeSlot().isAvailable());

        return new AppointmentResponseBean(
                booking.getId(),
                patientBean,
                doctorBean,
                specializationBean,
                slotBean,
                booking.getStatus(),
                booking.getNotes());
    }
}
