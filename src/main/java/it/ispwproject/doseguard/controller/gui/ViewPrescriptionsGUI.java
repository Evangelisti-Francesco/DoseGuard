package it.ispwproject.doseguard.controller.gui;

import it.ispwproject.doseguard.bean.PrescriptionBean;
import it.ispwproject.doseguard.controller.applicativo.PrescriptionController;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Doctor;
import it.ispwproject.doseguard.model.Patient;
import it.ispwproject.doseguard.model.Pharmacist;
import it.ispwproject.doseguard.model.User;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import it.ispwproject.doseguard.view.gui.ViewPrescriptionsGUIView;
import javafx.stage.Stage;

import java.util.List;

public class ViewPrescriptionsGUI {

    private final Stage stage;
    private final PrescriptionController prescriptionController = new PrescriptionController();
    private final ViewPrescriptionsGUIView view = new ViewPrescriptionsGUIView();

    public ViewPrescriptionsGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        User loggedUser = SessionManager.getInstance().getLoggedUser();

        if (loggedUser instanceof Patient patient) {
            view.searchSectionBox.setVisible(false);
            view.searchSectionBox.setManaged(false);
            loadPrescriptionsForPatientId(patient.getId());

        } else if (loggedUser instanceof Doctor || loggedUser instanceof Pharmacist) {
            // Se medico o farmacista
            view.searchSectionBox.setVisible(true);
            view.searchSectionBox.setManaged(true);

            view.searchBtn.setOnAction(e -> {
                String fiscalCode = view.fiscalCodeField.getText();
                if (fiscalCode == null || fiscalCode.isBlank()) {
                    view.setError("Inserire un codice fiscale valido.");
                    return;
                }
                loadPrescriptionsForFiscalCode(fiscalCode.trim());
            });
        }

        bindActions();
        stage.setScene(GUIUtils.createScene(view.buildRoot()));
        stage.show();
    }


    private void loadPrescriptionsForPatientId(int patientId) {
        view.clearError();
        try {
            List<PrescriptionBean> prescriptions = prescriptionController.getPrescriptionsByPatientId(patientId);
            view.renderPrescriptions(prescriptions);
        } catch (DAOException e) {
            view.setError("Errore nel recupero delle prescrizioni: " + e.getMessage());
        }
    }

    // Ricerca tramite Codice Fiscale (per Medici e Farmacisti)
    private void loadPrescriptionsForFiscalCode(String fiscalCode) {
        view.clearError();
        try {
            List<PrescriptionBean> prescriptions = prescriptionController.getPatientPrescriptions(fiscalCode);
            view.renderPrescriptions(prescriptions);
        } catch (DAOException e) {
            view.setError("Errore nel recupero delle prescrizioni: " + e.getMessage());
        }
    }

    private void bindActions() {
        view.goBackBtn.setOnAction(e -> {
            User loggedUser = SessionManager.getInstance().getLoggedUser();
            if (loggedUser instanceof Patient) {
                MainGUI.showDashboardPatient();
            } else if (loggedUser instanceof Doctor) {
                MainGUI.showDashboardDoctor();
            } else if (loggedUser instanceof Pharmacist) {
                MainGUI.showDashboardPharmacist();
            } else {
                MainGUI.showLogin();
            }
        });
    }
}