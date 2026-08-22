package it.ispwproject.doseguard.controller.gui;

import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import it.ispwproject.doseguard.view.gui.DashboardPharmacistGUIView;
import javafx.stage.Stage;

public class DashboardPharmacistGUI {

    private final Stage stage;
    private final DashboardPharmacistGUIView view = new DashboardPharmacistGUIView();

    public DashboardPharmacistGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        String nome = SessionManager.getInstance().getLoggedUser().getName();

        view.dispenseBtn.setOnAction(e -> new DispenseMedicationGUI(stage).show());

        stage.setScene(GUIUtils.createScene(view.buildRoot(nome, this::handleLogout)));
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
