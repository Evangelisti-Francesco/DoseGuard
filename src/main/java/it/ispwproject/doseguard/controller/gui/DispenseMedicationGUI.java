package it.ispwproject.doseguard.controller.gui;

import it.ispwproject.doseguard.bean.PrescriptionBean;
import it.ispwproject.doseguard.controller.applicativo.PrescriptionController;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import it.ispwproject.doseguard.view.gui.DispenseMedicationGUIView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DispenseMedicationGUI {

    private final Stage stage;
    private final PrescriptionController prescriptionController = new PrescriptionController();
    private final DispenseMedicationGUIView view = new DispenseMedicationGUIView();

    public DispenseMedicationGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        BorderPane root = view.buildRoot(MainGUI::showDashboardPharmacist);

        view.searchBtn.setOnAction(e -> handleSearch());
        view.codeField.setOnAction(e -> handleSearch());

        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    private void handleSearch() {
        view.clearError();
        String text = view.codeField.getText().trim();

        if (text.isBlank()) {
            view.setError("Inserire un ID ricetta valido.");
            return;
        }

        int prescriptionId;
        try {
            prescriptionId = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            view.setError("L'ID della ricetta deve essere un numero intero.");
            return;
        }

        try {
            PrescriptionBean prescription = prescriptionController.getPrescriptionById(prescriptionId);
            VBox card = view.getPrescriptionCard();

            if (prescription == null) {
                view.setError("Nessuna prescrizione trovata con l'ID fornito.");
                card.setVisible(false);
                card.setManaged(false);
            } else {
                view.buildPrescriptionCard(card, prescription, this::handleDispense);
            }
        } catch (DAOException ex) {
            view.setError("Errore durante la ricerca: " + ex.getMessage());
        }
    }

    private void handleDispense(PrescriptionBean prescription) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Conferma Erogazione");
        confirm.setHeaderText(null);
        confirm.setContentText("Confermi l'erogazione del farmaco \"" +
                prescription.getDrug() + "\" per il codice fiscale " +
                prescription.getPatientFiscalCode() + "?");

        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    int pharmacistId = SessionManager.getInstance().getLoggedUser().getId();
                    prescriptionController.fulfillPrescription(prescription.getId(), pharmacistId);

                    showInfo("Farmaco erogato con successo!");
                    view.getPrescriptionCard().setVisible(false);
                    view.getPrescriptionCard().setManaged(false);
                    view.codeField.clear();
                } catch (DAOException ex) {
                    view.setError("Errore durante l'erogazione: " + ex.getMessage());
                }
            }
        });
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Operazione Completata");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}