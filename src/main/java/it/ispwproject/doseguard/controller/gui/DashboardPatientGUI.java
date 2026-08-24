package it.ispwproject.doseguard.controller.gui;

import it.ispwproject.doseguard.model.User;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import it.ispwproject.doseguard.view.gui.DashboardPatientGUIView;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DashboardPatientGUI {

    private final Stage stage;
    private final DashboardPatientGUIView view;
    private final User currentUser;

    public DashboardPatientGUI(Stage stage) {
        this.stage = stage;
        this.view = new DashboardPatientGUIView();
        this.currentUser = SessionManager.getInstance().getLoggedUser();
    }

    public void show() {
        BorderPane root = view.buildPatientDashboardRoot(
                e ->  new BookAppointmentGUI(stage).show(),
                e -> new ViewBookingsGUI(stage).show(),
                e -> new CancelBookingGUI(stage).show(),
                e -> new ViewMedicationsGUI(stage).show(),
                e -> new ViewPrescriptionsGUI(stage).show(),
                e -> new EditProfileGUI(stage).show(),
                e -> new LoginGUI(stage).show()
        );

        Scene scene = new Scene(root, 1200, 750);
        stage.setScene(scene);
        stage.setTitle("DoseGuard - Dashboard Paziente");
        stage.show();
    }
}