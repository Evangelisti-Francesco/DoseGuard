package it.ispwproject.doseguard.view.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class DashboardPatientGUIView extends DashboardGUIView {

    public final Button btnBookAppointment   = new Button();
    public final Button btnViewAppointments  = new Button();
    public final Button btnCancelAppointment = new Button();
    public final Button btnMedicines         = new Button();
    public final Button btnPrescriptions     = new Button();
    public final Button btnEditProfile       = new Button();

    public BorderPane buildPatientDashboardRoot(
            EventHandler<ActionEvent> onBook,
            EventHandler<ActionEvent> onViewAppts,
            EventHandler<ActionEvent> onCancelAppt,
            EventHandler<ActionEvent> onMedicines,
            EventHandler<ActionEvent> onPrescriptions,
            EventHandler<ActionEvent> onEditProfile,
            EventHandler<ActionEvent> onLogout) {

        // Associazione eventi
        btnBookAppointment.setOnAction(onBook);
        btnViewAppointments.setOnAction(onViewAppts);
        btnCancelAppointment.setOnAction(onCancelAppt);
        btnMedicines.setOnAction(onMedicines);
        btnPrescriptions.setOnAction(onPrescriptions);
        btnEditProfile.setOnAction(onEditProfile);

        // Creazione delle card tramite il metodo della classe padre
        VBox c1 = createFeatureCard(btnBookAppointment, "Prenota Visita", "Prenota una nuova visita medica.");
        VBox c2 = createFeatureCard(btnViewAppointments, "I Miei Appuntamenti", "Visualizza i tuoi appuntamenti.");
        VBox c3 = createFeatureCard(btnCancelAppointment, "Annulla Visita", "Cancella un appuntamento.");
        VBox c4 = createFeatureCard(btnMedicines, "Terapia e Farmaci", "Gestisci i tuoi farmaci e dosaggi.");
        VBox c5 = createFeatureCard(btnPrescriptions, "Ricette Mediche", "Consulta le tue ricette digitali.");
        VBox c6 = createFeatureCard(btnEditProfile, "Modifica Profilo", "Aggiorna il tuo account.");

        // Sfrutta il layout unificato del padre passandogli il titolo del ruolo, il logout e le card
        return buildDashboardRoot("Home - Menu Paziente", onLogout, c1, c2, c3, c4, c5, c6);
    }
}