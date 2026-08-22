package it.ispwproject.doseguard.controller.gui;

import it.ispwproject.doseguard.controller.applicativo.LoginController;
import it.ispwproject.doseguard.controller.applicativo.LoginController.LoginResult;
import it.ispwproject.doseguard.exception.LoginException;
import it.ispwproject.doseguard.view.gui.LoginGUIView;
import javafx.stage.Stage;

public class LoginGUI {

    private final Stage stage;
    private final LoginController loginController = new LoginController();
    private final LoginGUIView view = new LoginGUIView();

    public LoginGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        stage.setScene(GUIUtils.createScene(
                view.buildRoot(this::handleLogin, MainGUI::showRegistration)));
        stage.show();
    }

    private void handleLogin() {
        String email = view.emailField.getText().trim();
        String password = view.passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            view.setError("Inserisci sia email che password.");
            return;
        }

        try {
            LoginResult result = loginController.login(email, password);

            switch (result) {
                case SUCCESSO_PATIENT    -> MainGUI.showDashboardPatient();
                case SUCCESSO_DOCTOR     -> MainGUI.showDashboardDoctor();
                case SUCCESSO_PHARMACIST -> MainGUI.showDashboardPharmacist();
            }
        } catch (LoginException e) {
            view.setError(e.getMessage());
        }
    }
}
