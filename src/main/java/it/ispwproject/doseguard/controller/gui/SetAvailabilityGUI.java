package it.ispwproject.doseguard.controller.gui;

import it.ispwproject.doseguard.bean.TimeSlotBean;
import it.ispwproject.doseguard.controller.applicativo.AvailabilityController;
import it.ispwproject.doseguard.exception.AvailabilityException;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.view.gui.SetAvailabilityGUIView;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class SetAvailabilityGUI {

    private final Stage stage;
    private final AvailabilityController availabilityController = new AvailabilityController();
    private final SetAvailabilityGUIView view = new SetAvailabilityGUIView();

    public SetAvailabilityGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        view.saveBtn.setOnAction(e -> handleSave());
        stage.setScene(GUIUtils.createScene(
                view.buildRoot(MainGUI::showDashboardDoctor)));
        stage.show();
    }

    private void handleSave() {
        view.clearError();

        LocalDate date = view.datePicker.getValue();
        if (date == null) {
            view.setError("Seleziona una data valida.");
            return;
        }

        LocalTime startTime;
        try {
            startTime = LocalTime.parse(view.startTimeField.getText().trim());
        } catch (DateTimeParseException e) {
            view.setError("Formato ora inizio non valido. Usa HH:MM.");
            return;
        }

        try {
            // Creo lo slot
            availabilityController.addSlot(new TimeSlotBean(0, date, startTime, true));

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Slot aggiunto");
            alert.setHeaderText(null);
            alert.setContentText("✓ Slot di disponibilità aggiunto con successo!");
            alert.showAndWait();

            MainGUI.showDashboardDoctor();
        } catch (DAOException | AvailabilityException e) {
            view.setError("Errore: " + e.getMessage());
        }
    }
}
