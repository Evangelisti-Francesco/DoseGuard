package it.ispwproject.doseguard.controller.cli;

import it.ispwproject.doseguard.bean.AppointmentResponseBean;
import it.ispwproject.doseguard.controller.applicativo.BookingController;
import it.ispwproject.doseguard.enumerator.AppointmentStatus;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import it.ispwproject.doseguard.pattern.state.AbstractCLIState;
import it.ispwproject.doseguard.pattern.state.CLIStateMachine;
import it.ispwproject.doseguard.view.cli.CancelBookingView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

public class CancelBookingCLI extends AbstractCLIState {

    private final BookingController bookingController = new BookingController();
    private final CancelBookingView view = new CancelBookingView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        int patientId = SessionManager.getInstance().getLoggedUser().getId();
        try {
            List<AppointmentResponseBean> cancellable = bookingController
                    .getPatientBookings(patientId)
                    .stream()
                    .filter(b -> b.getStatus() == AppointmentStatus.CONFIRMED)
                    .filter(b -> b.getSlot().getDate().isAfter(LocalDate.now(ZoneId.systemDefault())) ||
                            (b.getSlot().getDate().isEqual(LocalDate.now(ZoneId.systemDefault())) &&
                                    b.getSlot().getStartTime().isAfter(LocalTime.now(ZoneId.systemDefault()))))
                    .toList();

            if (cancellable.isEmpty()) {
                view.mostraMessaggio("Nessun appuntamento attivo da annullare.");
                goBack(context);
                return;
            }

            view.mostraPrenotazioniAnnullabili(cancellable);
            int choice = view.chiediScelta("Seleziona l'appuntamento da annullare", 0, cancellable.size());
            if (choice == 0) { goBack(context); return; }

            AppointmentResponseBean selected = cancellable.get(choice - 1);
            view.mostraRiepilogo(selected);

            if (!view.chiediConferma("Sei sicuro di voler annullare l'appuntamento?")) {
                view.mostraMessaggio("Operazione annullata.");
                goBack(context);
                return;
            }

            bookingController.cancelBooking(selected.getId(), patientId);
            view.mostraSuccesso();

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }
        goBack(context);
    }
}
