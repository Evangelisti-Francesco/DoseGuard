package it.ispwproject.doseguard.controller.gui;

import it.ispwproject.doseguard.bean.MedicationBean;
import it.ispwproject.doseguard.controller.applicativo.MedicationController;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.view.gui.ViewMedicationsGUIView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class ViewMedicationsGUI {

    private final Stage stage;
    private final MedicationController medicationController = new MedicationController();
    private final ViewMedicationsGUIView view = new ViewMedicationsGUIView();

    public ViewMedicationsGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        loadMedications();
        bindActions();

        stage.setScene(GUIUtils.createScene(view.buildRoot()));
        stage.show();
    }

    private void loadMedications() {
        view.clearError();
        try {
            List<MedicationBean> medications = medicationController.getPatientMedications();
            view.renderMedications(medications, this::markAsTakenHandler);
        } catch (DAOException e) {
            view.setError("Errore caricamento farmaci: " + e.getMessage());
        }
    }

    private void markAsTakenHandler(MedicationBean medication) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Conferma assunzione");
        confirmAlert.setHeaderText("Vuoi confermare l'assunzione di questo farmaco?");
        confirmAlert.setContentText("Farmaco: " + medication.getName() + "\nDosaggio: " + medication.getDosage());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                medicationController.markMedicationAsTaken(medication.getId());

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Successo");
                successAlert.setHeaderText(null);
                successAlert.setContentText("✓ Assunzione del farmaco registrata con successo.");
                successAlert.showAndWait();

                // Ricarica la lista aggiornata
                loadMedications();

            } catch (DAOException e) {
                view.setError("Errore durante la registrazione: " + e.getMessage());
            }
        }
    }

    private void bindActions() {
        view.goBackBtn.setOnAction(e -> MainGUI.showDashboardPatient());
    }
}