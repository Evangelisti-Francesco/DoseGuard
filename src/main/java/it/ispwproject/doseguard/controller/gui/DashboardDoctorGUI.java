package it.ispwproject.doseguard.controller.gui;

import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import it.ispwproject.doseguard.view.gui.DashboardDoctorGUIView;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DashboardDoctorGUI {

    private final Stage stage;
    private final DashboardDoctorGUIView view = new DashboardDoctorGUIView();

    public DashboardDoctorGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        BorderPane root = view.buildDoctorDashboardRoot(
                e ->  new CreatePrescriptionGUI(stage).show(),
                e -> new ViewPrescriptionsGUI(stage).show(),
                e -> new SetAvailabilityGUI(stage).show(),
                e -> new ViewSlotsGUI(stage).show(),
                e -> new ManagePatientsGUI(stage).show(),
                e -> new EditProfileGUI(stage).show(),
                e -> handleLogout()
        );

        stage.setScene(new Scene(root, 1200, 750));
        stage.setTitle("DoseGuard - Dashboard Medico");
        stage.show();
    }

    private void handleLogout() {
        try {
            it.ispwproject.doseguard.dao.ConnectionFactory.clearRole();
        } catch (java.sql.SQLException ex) {
            /* ignora */
        }
        SessionManager.getInstance().clearSession();
        MainGUI.showLogin();
    }
}