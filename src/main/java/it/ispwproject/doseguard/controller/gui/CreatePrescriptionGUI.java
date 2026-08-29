package it.ispwproject.doseguard.controller.gui;

import it.ispwproject.doseguard.bean.PatientBean;
import it.ispwproject.doseguard.controller.applicativo.PatientManagementController;
import it.ispwproject.doseguard.controller.applicativo.PrescriptionController;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.view.gui.CreatePrescriptionGUIView;
import javafx.stage.Stage;

import java.util.List;

public class CreatePrescriptionGUI {

    private final Stage stage;
    private final PrescriptionController prescriptionController = new PrescriptionController();
    private final PatientManagementController patientManagementController = new PatientManagementController();
    private final CreatePrescriptionGUIView view = new CreatePrescriptionGUIView();

    public CreatePrescriptionGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        try {
            List<PatientBean> patients = patientManagementController.getPatients();
            view.patientCombo.getItems().setAll(patients);
        } catch (DAOException e) {
            view.errorLabel.setStyle("-fx-text-fill: #d9534f;");
            view.errorLabel.setText("Errore nel caricamento dei pazienti: " + e.getMessage());
        }

        view.submitBtn.setOnAction(e -> handleCreatePrescription());

        stage.setScene(GUIUtils.createScene(view.buildRoot(MainGUI::showDashboardDoctor)));
        stage.show();
    }

    private void handleCreatePrescription() {
        view.errorLabel.setText("");

        PatientBean selectedPatient = view.patientCombo.getValue();
        String drug = view.drugField.getText();
        String dosage = view.dosageField.getText();
        String frequency = view.frequencyField.getText();

        if (selectedPatient == null || drug == null || drug.isBlank() ||
                dosage == null || dosage.isBlank() || frequency == null || frequency.isBlank()) {
            view.errorLabel.setStyle("-fx-text-fill: #d9534f;");
            view.errorLabel.setText("Seleziona un paziente e compila tutti i campi.");
            return;
        }

        try {
            // Passa l'ID del paziente
            prescriptionController.createPrescription(selectedPatient.getId(), drug.trim(), dosage.trim(), frequency.trim());

            view.errorLabel.setStyle("-fx-text-fill: #2e7d32;"); // Verde successo
            view.errorLabel.setText("Prescrizione creata e registrata con successo!");

            view.patientCombo.setValue(null);
            view.drugField.clear();
            view.dosageField.clear();
            view.frequencyField.clear();

        } catch (DAOException e) {
            view.errorLabel.setStyle("-fx-text-fill: #d9534f;");
            view.errorLabel.setText("Errore durante la creazione: " + e.getMessage());
        }
    }
}