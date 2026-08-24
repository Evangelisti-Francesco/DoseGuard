package it.ispwproject.doseguard.controller.gui;

import it.ispwproject.doseguard.bean.AppointmentResponseBean;
import it.ispwproject.doseguard.controller.applicativo.BookingController;
import it.ispwproject.doseguard.enumerator.AppointmentStatus;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import it.ispwproject.doseguard.view.gui.CancelBookingGUIView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

public class CancelBookingGUI {

    private final Stage stage;
    private final BookingController bookingController = new BookingController();
    private final CancelBookingGUIView view = new CancelBookingGUIView();

    public CancelBookingGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        loadCancellableAppointments();
        bindActions();

        stage.setScene(GUIUtils.createScene(view.buildRoot()));
        stage.show();
    }

    private void loadCancellableAppointments() {
        view.clearError();
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

            view.renderCancellableAppointments(cancellable, this::confirmAndCancel);

        } catch (DAOException e) {
            view.setError("Errore caricamento appuntamenti: " + e.getMessage());
        }
    }

    private void confirmAndCancel(AppointmentResponseBean selected) {
        int patientId = SessionManager.getInstance().getLoggedUser().getId();

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Conferma annullamento");
        confirmAlert.setHeaderText("Sei sicuro di voler annullare questo appuntamento?");
        confirmAlert.setContentText(
                "Specializzazione: " + selected.getSpecialization().getName() + "\n" +
                        "Medico: Dott. " + selected.getDoctor().getName() + " " + selected.getDoctor().getSurname() + "\n" +
                        "Data: " + selected.getSlot().getDate() + " ore " + selected.getSlot().getStartTime()
        );

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                bookingController.cancelBooking(selected.getId(), patientId);

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Successo");
                successAlert.setHeaderText(null);
                successAlert.setContentText("✓ Appuntamento annullato con successo.");
                successAlert.showAndWait();

                // Ricarica la lista aggiornata
                loadCancellableAppointments();

            } catch (DAOException e) {
                view.setError("Errore durante l'annullamento: " + e.getMessage());
            }
        }
    }

    private void bindActions() {
        view.goBackBtn.setOnAction(e -> MainGUI.showDashboardPatient());
    }
}
