package it.ispwproject.doseguard.view.gui;

import it.ispwproject.doseguard.bean.PrescriptionBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class DispenseMedicationGUIView extends PageGUIView {

    public final TextField codeField  = new TextField();
    public final Button    searchBtn  = new Button("Cerca Ricetta");
    public final Label     errorLabel = buildErrorLabel();

    public DispenseMedicationGUIView() {
        codeField.getStyleClass().add("text-field");
        codeField.setPromptText("Inserisci ID ricetta...");
        codeField.setPrefWidth(280);

        searchBtn.getStyleClass().add("save-button");
    }

    public void setError(String message) {
        errorLabel.setText(message);
    }

    public void clearError() {
        errorLabel.setText("");
    }

    public BorderPane buildRoot(Runnable onBack) {
        BorderPane root = buildShell("Eroga Farmaco", onBack);

        VBox content = new VBox(16);
        content.setPadding(new Insets(28, 48, 28, 48));
        content.setAlignment(Pos.TOP_CENTER);

        VBox searchCard = new VBox(12);
        searchCard.getStyleClass().add("info-card");
        searchCard.setMaxWidth(640);
        searchCard.setAlignment(Pos.CENTER_LEFT);

        Label searchLabel = new Label("Ricerca Prescrizione");
        searchLabel.getStyleClass().add("small-label");

        HBox searchRow = new HBox(12, codeField, searchBtn);
        searchRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(codeField, Priority.ALWAYS);

        searchCard.getChildren().addAll(searchLabel, searchRow);

        VBox prescriptionCard = new VBox(12);
        prescriptionCard.setMaxWidth(640);
        prescriptionCard.setVisible(false);
        prescriptionCard.setManaged(false);

        codeField.setUserData(prescriptionCard);

        content.getChildren().addAll(searchCard, prescriptionCard, errorLabel);

        root.setCenter(transparentScroll(content));
        return root;
    }

    public VBox getPrescriptionCard() {
        return (VBox) codeField.getUserData();
    }

    public void buildPrescriptionCard(VBox card, PrescriptionBean prescription, Consumer<PrescriptionBean> onDispense) {
        card.getChildren().clear();
        card.setVisible(true);
        card.setManaged(true);

        HBox infoCard = new HBox(16);
        infoCard.getStyleClass().add("info-card");
        infoCard.setAlignment(Pos.CENTER_LEFT);

        VBox details = new VBox(6);
        HBox.setHgrow(details, Priority.ALWAYS);

        Label medLabel = new Label("Farmaco: " + prescription.getDrug());
        medLabel.getStyleClass().add("welcome-label");

        Label patientLabel = new Label("CF Paziente: " + prescription.getPatientFiscalCode());
        patientLabel.getStyleClass().add("register-label");

        Label doctorLabel = new Label("Medico Prescrittore: " + prescription.getDoctorFullName());
        doctorLabel.getStyleClass().add("info-text");

        Label posologyLabel = new Label("Dosaggio: " + prescription.getDosage() + "   Frequenza: " + prescription.getFrequency());
        posologyLabel.getStyleClass().add("info-text");

        details.getChildren().addAll(medLabel, patientLabel, doctorLabel, posologyLabel);

        if (prescription.getIssueDate() != null) {
            String formattedDate = prescription.getIssueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            Label dateLabel = new Label("Data Emissione: " + formattedDate);
            dateLabel.getStyleClass().add("info-text");
            details.getChildren().add(dateLabel);
        }

        Button dispenseBtn = new Button("Conferma Erogazione");
        dispenseBtn.getStyleClass().add("save-button");
        dispenseBtn.setPrefHeight(40);
        dispenseBtn.setOnAction(e -> onDispense.accept(prescription));

        infoCard.getChildren().addAll(details, dispenseBtn);
        card.getChildren().add(infoCard);
    }
}