package it.ispwproject.doseguard.view.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class DashboardDoctorGUIView extends DashboardGUIView {

    public final Button btnCreatePrescription  = new Button();
    public final Button btnViewPrescriptions   = new Button();
    public final Button btnSetAvailability     = new Button();
    public final Button btnViewSlots           = new Button();
    public final Button btnManagePatients      = new Button();
    public final Button btnProfile             = new Button();

    public BorderPane buildDoctorDashboardRoot(
            EventHandler<ActionEvent> onCreatePrescription,
            EventHandler<ActionEvent> onViewPrescriptions,
            EventHandler<ActionEvent> onSetAvailability,
            EventHandler<ActionEvent> onViewSlots,
            EventHandler<ActionEvent> onManagePatients,
            EventHandler<ActionEvent> onProfile,
            EventHandler<ActionEvent> onLogout) {

        btnCreatePrescription.setOnAction(onCreatePrescription);
        btnViewPrescriptions.setOnAction(onViewPrescriptions);
        btnSetAvailability.setOnAction(onSetAvailability);
        btnViewSlots.setOnAction(onViewSlots);
        btnManagePatients.setOnAction(onManagePatients);
        btnProfile.setOnAction(onProfile);

        // Creazione delle card per ciascuna funzionalità
        VBox c1 = createFeatureCard(btnCreatePrescription, "Crea prescrizione", "Emetti una nuova prescrizione medica.");
        VBox c2 = createFeatureCard(btnViewPrescriptions, "Visualizza prescrizioni", "Consulta le prescrizioni dei pazienti.");
        VBox c3 = createFeatureCard(btnSetAvailability, "Imposta Disponibilità", "Definisci i tuoi slot orari e i giorni liberi.");
        VBox c4 = createFeatureCard(btnViewSlots, "Visualizza Slot", "Controlla gli slot di disponibilità inseriti.");
        VBox c5 = createFeatureCard(btnManagePatients, "Gestisci Pazienti", "Visualizza e gestisci l'elenco dei tuoi pazienti.");
        VBox c6 = createFeatureCard(btnProfile, "Profilo", "Visualizza e gestisci le informazioni del profilo.");

        // Passiamo direttamente le 6 VBox al metodo della classe padre
        return buildDashboardRoot("Home - Menu Medico", onLogout, c1, c2, c3, c4, c5, c6);
    }
}