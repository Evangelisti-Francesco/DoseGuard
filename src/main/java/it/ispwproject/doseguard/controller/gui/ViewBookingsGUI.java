package it.ispwproject.doseguard.controller.gui;

import it.ispwproject.doseguard.bean.AppointmentResponseBean;
import it.ispwproject.doseguard.controller.applicativo.BookingController;
import it.ispwproject.doseguard.enumerator.AppointmentStatus;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import it.ispwproject.doseguard.view.gui.ViewBookingsGUIView;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

public class ViewBookingsGUI {

    private final Stage stage;
    private final BookingController bookingController = new BookingController();
    private final ViewBookingsGUIView view = new ViewBookingsGUIView();

    private List<AppointmentResponseBean> confirmed;
    private List<AppointmentResponseBean> cancelled;
    private List<AppointmentResponseBean> past;

    public ViewBookingsGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        loadData();
        setupToggleGroup();
        bindActions();

        stage.setScene(GUIUtils.createScene(view.buildRoot()));
        stage.show();
    }

    private void loadData() {
        int patientId = SessionManager.getInstance().getLoggedUser().getId();
        try {
            List<AppointmentResponseBean> all = bookingController.getPatientBookings(patientId);
            past = bookingController.getPatientPastBookings(patientId);

            confirmed = all.stream()
                    .filter(b -> b.getStatus() == AppointmentStatus.CONFIRMED)
                    .filter(b -> b.getSlot().getDate().isAfter(LocalDate.now(ZoneId.systemDefault())) ||
                            (b.getSlot().getDate().isEqual(LocalDate.now(ZoneId.systemDefault())) &&
                                    b.getSlot().getStartTime().isAfter(LocalTime.now(ZoneId.systemDefault()))))
                    .sorted((a, b) -> a.getSlot().getDate().compareTo(b.getSlot().getDate()))
                    .toList();

            cancelled = all.stream()
                    .filter(b -> b.getStatus() == AppointmentStatus.CANCELLED)
                    .sorted((a, b) -> a.getSlot().getDate().compareTo(b.getSlot().getDate()))
                    .toList();

            // Aggiorna i contatori sui tab
            view.confirmedTabBtn.setText("Confermati (" + confirmed.size() + ")");
            view.cancelledTabBtn.setText("Cancellati (" + cancelled.size() + ")");
            view.pastTabBtn.setText("Scaduti (" + past.size() + ")");

        } catch (DAOException e) {
            view.setError("Errore caricamento appuntamenti: " + e.getMessage());
        }
    }

    private void setupToggleGroup() {
        // Collega i pulsanti a un ToggleGroup logico per fare in modo che se ne selezioni uno, gli altri si spengono
        javafx.scene.control.ToggleGroup group = new javafx.scene.control.ToggleGroup();
        view.confirmedTabBtn.setToggleGroup(group);
        view.cancelledTabBtn.setToggleGroup(group);
        view.pastTabBtn.setToggleGroup(group);

        // Seleziona di default i confermati all'apertura
        view.confirmedTabBtn.setSelected(true);
        view.renderAppointments(confirmed, "Non hai appuntamenti confermati.");

        // Azioni al cambio tab
        view.confirmedTabBtn.setOnAction(e -> {
            if (view.confirmedTabBtn.isSelected()) {
                view.renderAppointments(confirmed, "Non hai appuntamenti confermati.");
            }
        });

        view.cancelledTabBtn.setOnAction(e -> {
            if (view.cancelledTabBtn.isSelected()) {
                view.renderAppointments(cancelled, "Non hai appuntamenti cancellati.");
            }
        });

        view.pastTabBtn.setOnAction(e -> {
            if (view.pastTabBtn.isSelected()) {
                view.renderAppointments(past, "Nessun appuntamento scaduto.");
            }
        });
    }

    private void bindActions() {
        view.goBackBtn.setOnAction(e -> MainGUI.showDashboardPatient());
    }
}
