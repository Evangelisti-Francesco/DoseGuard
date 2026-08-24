package it.ispwproject.doseguard.controller.gui;

import it.ispwproject.doseguard.dao.ConnectionFactory;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import it.ispwproject.doseguard.view.gui.DashboardPharmacistGUIView;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DashboardPharmacistGUI {

    private final Stage stage;
    private final DashboardPharmacistGUIView view = new DashboardPharmacistGUIView();

    public DashboardPharmacistGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        String pharmacistName = "";
        try {
            if (SessionManager.getInstance().getLoggedUser() != null) {
                pharmacistName = SessionManager.getInstance().getLoggedUser().getName();
            }
        } catch (Exception e) {
            // Gestione silenziosa se il nome non è disponibile
        }

        BorderPane root = view.buildPharmacistDashboardRoot(
                e -> new ViewPrescriptionsGUI(stage).show(),
                e -> new EditProfileGUI(stage).show(),
                e -> handleLogout(),
                pharmacistName
        );

        stage.setScene(new Scene(root, 1200, 750));
        stage.setTitle("DoseGuard - Dashboard Farmacista");
        stage.show();
    }

    private void handleLogout() {
        try {
            ConnectionFactory.clearRole();
        } catch (java.sql.SQLException ex) {
            /* ignora */
        }
        SessionManager.getInstance().clearSession();
        MainGUI.showLogin();
    }
}