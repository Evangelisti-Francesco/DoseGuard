package it.ispwproject.doseguard.controller.gui;

import it.ispwproject.doseguard.bean.RegistrationBean;
import it.ispwproject.doseguard.bean.SpecializationBean;
import it.ispwproject.doseguard.controller.applicativo.RegistrationController;
import it.ispwproject.doseguard.enumerator.Role;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.exception.RegistrationException;
import it.ispwproject.doseguard.view.gui.RegistrationGUIView;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.List;

public class RegistrationGUI {

    private final Stage stage;
    private final RegistrationController registrationController = new RegistrationController();
    private final RegistrationGUIView view = new RegistrationGUIView();

    public RegistrationGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        view.registerBtn.setOnAction(e -> handleRegistration());
        stage.setScene(GUIUtils.createScene(view.buildRoot(MainGUI::showLogin)));
        stage.show();
    }

    private void handleRegistration() {
        RegistrationBean bean = new RegistrationBean();
        bean.setName(view.nameField.getText().trim());
        bean.setSurname(view.surnameField.getText().trim());
        bean.setFiscalCode(view.fiscalCodeField.getText().trim());
        bean.setEmail(view.emailField.getText().trim());
        bean.setPassword(view.passwordField.getText().trim());
        bean.setConfirmPassword(view.confirmPasswordField.getText().trim());

        // Selezione del ruolo
        if (view.doctorRadio.isSelected()) {
            bean.setRole(Role.DOCTOR);
            String specName = view.specializationComboBox.getValue();
            SpecializationBean specBean = new SpecializationBean();
            specBean.setName(specName);
            bean.setSpecializations(List.of(specBean));
        } else if (view.pharmacistRadio.isSelected()) {
            bean.setRole(Role.PHARMACIST);
        } else {
            bean.setRole(Role.PATIENT);
        }

        try {
            registrationController.register(bean);
            showSuccess();
        } catch (RegistrationException e) {
            view.setError(e.getMessage());
        } catch (DAOException e) {
            view.setError("Errore di sistema: " + e.getMessage());
        }
    }

    private void showSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registrazione completata");
        alert.setHeaderText(null);
        alert.setContentText("Registrazione completata con successo! Ora puoi effettuare il login.");
        alert.showAndWait();
        MainGUI.showLogin();
    }
}
