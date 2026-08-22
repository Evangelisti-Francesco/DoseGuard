package it.ispwproject.doseguard.controller.gui;

import it.ispwproject.doseguard.bean.*;
import it.ispwproject.doseguard.controller.applicativo.PatientManagementController;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.view.gui.ManagePatientsGUIView;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ManagePatientsGUI {

    private final Stage stage;
    private final PatientManagementController patientManagementController = new PatientManagementController();
    private final ManagePatientsGUIView view = new ManagePatientsGUIView();

    public ManagePatientsGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        BorderPane root = view.buildRoot(MainGUI::showDashboardDoctor);

        try {
            view.patientCombo.getItems().setAll(patientManagementController.getPatients());
        } catch (DAOException e) {
            view.errorLabel.setText("Errore: " + e.getMessage());
        }

        view.patientCombo.setOnAction(e -> {
            PatientBean selected = view.patientCombo.getValue();
            if (selected == null) return;
            loadPatientCard(selected);
        });

        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    private void loadPatientCard(PatientBean patient) {
        VBox card = view.getPatientCard();
        try {
            PatientProgressBean progress = patientManagementController.getProgress(patient.getId());
            List<TimeSlotBean> upcoming   = patientManagementController.getUpcomingAppointments(patient.getId());
            List<TimeSlotBean> completed  = patientManagementController.getCompletedAppointments(patient.getId());

            view.buildPatientCard(card, patient, progress, upcoming, completed,
                    notes -> handleUpdateProgress(patient, notes));

        } catch (DAOException e) {
            view.errorLabel.setText("Errore: " + e.getMessage());
        }
    }

    private void handleUpdateProgress(PatientBean patient, String notes) {
        if (notes.isBlank()) {
            view.errorLabel.setText("Le note non possono essere vuote.");
            return;
        }
        try {
            patientManagementController.updateProgress(new PatientProgressBean(patient, notes, null));
            showInfo("Quadro clinico aggiornato con successo.");
            loadPatientCard(patient);
        } catch (DAOException e) {
            view.errorLabel.setText("Errore: " + e.getMessage());
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Operazione completata");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}