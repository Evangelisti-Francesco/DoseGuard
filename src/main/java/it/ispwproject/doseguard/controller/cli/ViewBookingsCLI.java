package it.ispwproject.doseguard.controller.cli;

import it.ispwproject.doseguard.pattern.state.AbstractCLIState;
import it.ispwproject.doseguard.pattern.state.CLIStateMachine;

import it.ispwproject.doseguard.bean.AppointmentResponseBean;
import it.ispwproject.doseguard.controller.applicativo.BookingController;
import it.ispwproject.doseguard.enumerator.AppointmentStatus;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import it.ispwproject.doseguard.view.cli.ViewBookingsView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

public class ViewBookingsCLI extends AbstractCLIState {

    private final BookingController bookingController = new BookingController();
    private final ViewBookingsView view = new ViewBookingsView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        int patientId = SessionManager.getInstance().getLoggedUser().getId();
        try {
            List<AppointmentResponseBean> all  = bookingController.getPatientBookings(patientId);
            List<AppointmentResponseBean> past = bookingController.getPatientPastBookings(patientId);

            List<AppointmentResponseBean> confirmed = all.stream()
                    .filter(b -> b.getStatus() == AppointmentStatus.CONFIRMED)
                    .filter(b -> b.getSlot().getDate().isAfter(LocalDate.now(ZoneId.systemDefault())) ||
                            (b.getSlot().getDate().isEqual(LocalDate.now(ZoneId.systemDefault())) &&
                                    b.getSlot().getStartTime().isAfter(LocalTime.now(ZoneId.systemDefault()))))
                    .sorted((a, b) -> a.getSlot().getDate().compareTo(b.getSlot().getDate()))
                    .toList();

            List<AppointmentResponseBean> cancelled = all.stream()
                    .filter(b -> b.getStatus() == AppointmentStatus.CANCELLED)
                    .sorted((a, b) -> a.getSlot().getDate().compareTo(b.getSlot().getDate()))
                    .toList();

            boolean running = true;
            while (running) {
                view.mostraTab(confirmed.size(), cancelled.size(), past.size());
                int scelta = view.chiediScelta("Scelta", 0, 3);
                switch (scelta) {
                    case 1 -> view.mostraConfermate(confirmed);
                    case 2 -> view.mostraCancellate(cancelled);
                    case 3 -> view.mostraScadute(past);
                    case 0 -> running = false;
                    default -> view.mostraErrore("Scelta non valida.");
                }
            }
        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }
        goBack(context);
    }
}
