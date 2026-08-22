package it.ispwproject.doseguard.controller.gui;

import it.ispwproject.doseguard.bean.PrescriptionBean;
import it.ispwproject.doseguard.controller.applicativo.PrescriptionController;
import it.ispwproject.doseguard.controller.applicativo.UserController;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Patient;
import it.ispwproject.doseguard.model.User;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import it.ispwproject.doseguard.view.gui.DashboardPatientGUIView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class DashboardPatientGUI {

    private final Stage stage;
    private final PrescriptionController prescriptionController = new PrescriptionController();
    private final UserController userController = new UserController();
    private final DashboardPatientGUIView view = new DashboardPatientGUIView();

    private int weekOffset = 0;
    private final int[] weekOffRef = {0};

    public DashboardPatientGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        User user = SessionManager.getInstance().getLoggedUser();

        // Navbar
        HBox navbar = view.buildNavbar("Paziente", this::handleLogout);

        // Calendario delle assunzioni / visite
        VBox calendarSection = view.buildCalendarSection(
                () -> { weekOffset--; weekOffRef[0] = weekOffset; view.refreshCalendar(loadPrescriptions(), weekOffset); },
                () -> { weekOffset++; weekOffRef[0] = weekOffset; view.refreshCalendar(loadPrescriptions(), weekOffset); },
                () -> { weekOffset = 0; weekOffRef[0] = 0; view.refreshCalendar(loadPrescriptions(), weekOffset); }
        );
        List<PrescriptionBean> prescriptions = loadPrescriptions();
        view.bindCalendarWidth(prescriptions, weekOffRef);
        view.refreshCalendar(prescriptions, weekOffset);

        // Sezione destra (Azioni veloci e info utente)
        VBox actionButtons = view.buildActionButtons(
                e -> new BookAppointmentGUI(stage).show(),
                e -> new ViewPrescriptionsGUI(stage).show()
        );
        VBox accordion    = view.buildUserInfoAccordion(user, this::handleSaveEmail);
        VBox rightSection = view.buildRightSection(actionButtons, accordion);

        // Layout principale
        HBox body = new HBox(20);
        body.getStyleClass().add("doseguard-background");
        body.setPadding(new Insets(20, 24, 20, 24));
        body.setAlignment(Pos.CENTER);
        HBox.setHgrow(calendarSection, Priority.ALWAYS);
        body.getChildren().addAll(calendarSection, rightSection);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("doseguard-background");
        root.setTop(navbar);
        root.setCenter(body);

        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    // Azioni
    private List<PrescriptionBean> loadPrescriptions() {
        try {
            Patient patient = (Patient) SessionManager.getInstance().getLoggedUser();
            return prescriptionController.getPatientPrescriptions(patient.getFiscalCode());
        } catch (DAOException e) {
            return List.of();
        }
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

    private void handleSaveEmail(String newEmail) {
        try {
            userController.updateEmail(newEmail);
        } catch (DAOException ex) {
            // L'errore è visibile nell'etichetta aggiornata dalla View
        }
    }
}