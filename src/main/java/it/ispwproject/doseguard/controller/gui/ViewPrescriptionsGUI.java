package it.ispwproject.doseguard.controller.gui;

import it.ispwproject.doseguard.bean.PatientBean;
import it.ispwproject.doseguard.bean.PrescriptionBean;
import it.ispwproject.doseguard.controller.applicativo.PrescriptionController;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Patient;
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
        bindEvents();
        loadPrescriptions();
        stage.setScene(GUIUtils.createScene(view.buildRoot()));
        stage.show();
    }

    private void loadPrescriptions() {
        view.clearError();
        try {
            Patient p = (Patient) SessionManager.getInstance().getLoggedUser();
            PatientBean pb = new PatientBean(p.getId(), p.getName(), p.getSurname(), p.getEmail(), p.getFiscalCode());

            List<PrescriptionBean> prescriptions = prescriptionController.getPatientPrescriptions(pb.getFiscalCode());
            view.showPrescriptions(prescriptions, this::onPrescriptionSelected);
        } catch (DAOException e) {
            view.setError("Errore durante il recupero delle prescrizioni: " + e.getMessage());
        }
    }

    private void onPrescriptionSelected(PrescriptionBean prescription) {
        // Logica eventuale al click su una singola ricetta (es. mostra dettaglio o scarica PDF)
    }

    private void bindEvents() {
        view.goBackBtn.setOnAction(e -> MainGUI.showDashboardPatient());
    }
}
