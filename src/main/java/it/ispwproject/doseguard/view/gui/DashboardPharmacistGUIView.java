package it.ispwproject.doseguard.view.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class DashboardPharmacistGUIView extends DashboardGUIView {

    public final Button btnViewPrescriptions = new Button();
    public final Button btnProfile             = new Button();

    public BorderPane buildPharmacistDashboardRoot(
            EventHandler<ActionEvent> onViewPrescriptions,
            EventHandler<ActionEvent> onProfile,
            EventHandler<ActionEvent> onLogout,
            String pharmacistName) {

        btnViewPrescriptions.setOnAction(onViewPrescriptions);
        btnProfile.setOnAction(onProfile);

        VBox c1 = createFeatureCard(btnViewPrescriptions, "Visualizza Prescrizioni", "Consulta e gestisci le prescrizioni dei pazienti.");
        VBox c2 = createFeatureCard(btnProfile, "Profilo", "Visualizza e gestisci le informazioni del tuo profilo.");

        return buildDashboardRoot("Home - Menu Farmacista", onLogout, c1, c2);
    }
}