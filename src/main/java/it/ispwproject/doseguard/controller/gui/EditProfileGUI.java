package it.ispwproject.doseguard.controller.gui;

import it.ispwproject.doseguard.controller.applicativo.UserController;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.User;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import it.ispwproject.doseguard.view.gui.EditProfileGUIView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

public class EditProfileGUI {

    private final Stage stage;
    private final UserController userController = new UserController();
    private final EditProfileGUIView view = new EditProfileGUIView();

    public EditProfileGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        loadUserData();
        bindActions();

        stage.setScene(GUIUtils.createScene(view.buildRoot()));
        stage.show();
    }

    private void loadUserData() {
        User loggedUser = SessionManager.getInstance().getLoggedUser();
        if (loggedUser != null) {
            view.nameValueLabel.setText(loggedUser.getName());
            view.surnameValueLabel.setText(loggedUser.getSurname());
            view.emailField.setText(loggedUser.getEmail());
        }
    }

    private void bindActions() {
        // Gestione aggiornamento email
        view.updateEmailBtn.setOnAction(e -> {
            String newEmail = view.emailField.getText();
            if (newEmail == null || newEmail.isBlank()) {
                view.setError("Inserisci un'email valida.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Conferma modifica email");
            confirm.setHeaderText("Confermare il cambio email a " + newEmail + "?");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    userController.updateEmail(newEmail.trim());
                    SessionManager.getInstance().getLoggedUser().setEmail(newEmail.trim());
                    view.setSuccess("✓ Email aggiornata con successo.");
                } catch (DAOException ex) {
                    view.setError("Errore aggiornamento email: " + ex.getMessage());
                }
            }
        });

        // Tasto Go Back
        view.goBackBtn.setOnAction(e -> {
            User user = SessionManager.getInstance().getLoggedUser();
            if (user instanceof it.ispwproject.doseguard.model.Patient) {
                MainGUI.showDashboardPatient();
            } else if (user instanceof it.ispwproject.doseguard.model.Doctor) {
                MainGUI.showDashboardDoctor();
            } else if (user instanceof it.ispwproject.doseguard.model.Pharmacist) {
                MainGUI.showDashboardPharmacist();
            } else {
                MainGUI.showLogin();
            }
        });
    }
}
