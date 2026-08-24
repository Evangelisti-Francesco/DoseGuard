package it.ispwproject.doseguard.controller.gui;

import it.ispwproject.doseguard.bean.PatientBean;
import it.ispwproject.doseguard.bean.PatientProgressBean;
import it.ispwproject.doseguard.bean.TimeSlotBean;
import it.ispwproject.doseguard.controller.applicativo.PatientManagementController;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.view.gui.ManagePatientsGUIView;
import javafx.stage.Stage;

import java.util.List;

public class ManagePatientsGUI {

    private final Stage                       stage;
    private final PatientManagementController patientController = new PatientManagementController();
    private final ManagePatientsGUIView       view              = new ManagePatientsGUIView();

    public ManagePatientsGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        try {
            List<PatientBean> patients = patientController.getPatients();
            view.patientCombo.getItems().setAll(patients);

            if (patients.isEmpty()) {
                view.errorLabel.setText("Nessun paziente trovato.");
            }

        } catch (DAOException e) {
            view.errorLabel.setText("Errore nel caricamento dei pazienti: " + e.getMessage());
        }

        view.patientCombo.setOnAction(e -> {
            PatientBean selected = view.patientCombo.getValue();
            if (selected != null) {
                loadPatientDetails(selected);
            }
        });

        stage.setScene(GUIUtils.createScene(view.buildRoot(MainGUI::showDashboardDoctor)));
        stage.show();
    }

    private void loadPatientDetails(PatientBean patient) {
        view.errorLabel.setText("");
        try {
            List<TimeSlotBean> upcoming = patientController.getUpcomingAppointments(patient.getId());
            List<TimeSlotBean> completed = patientController.getCompletedAppointments(patient.getId());
            PatientProgressBean progress = patientController.getProgress(patient.getId());

            view.buildPatientCard(
                    view.getPatientCard(),
                    patient,
                    progress,
                    upcoming,
                    completed,
                    newNotes -> saveProgress(patient, newNotes)
            );

        } catch (DAOException e) {
            view.errorLabel.setText("Errore nel caricamento dei dati del paziente: " + e.getMessage());
        }
    }

    private void saveProgress(PatientBean patient, String notes) {
        if (notes == null || notes.isBlank()) {
            view.errorLabel.setText("Le note non possono essere vuote.");
            return;
        }
        try {
            patientController.updateProgress(new PatientProgressBean(patient, notes));
            view.errorLabel.setStyle("-fx-text-fill: #2e7d32;"); // Colore verde successo
            view.errorLabel.setText("Note e progressi aggiornati con successo.");
            loadPatientDetails(patient); // Ricarica la scheda aggiornata
        } catch (DAOException e) {
            view.errorLabel.setStyle("-fx-text-fill: #d9534f;");
            view.errorLabel.setText("Errore durante il salvataggio: " + e.getMessage());
        }
    }
}