package it.ispwproject.doseguard.view.gui;

import it.ispwproject.doseguard.bean.PatientBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CreatePrescriptionGUIView extends PageGUIView {

    public final ComboBox<PatientBean> patientCombo = new ComboBox<>();
    public final TextField             drugField      = new TextField();
    public final TextField             dosageField    = new TextField();
    public final TextField             frequencyField = new TextField();
    public final Button                submitBtn      = new Button("Crea Prescrizione");
    public final Label                 errorLabel     = buildErrorLabel();

    public CreatePrescriptionGUIView() {
        patientCombo.getStyleClass().add("combo-box");
        patientCombo.setPromptText("Seleziona paziente...");
        patientCombo.setMaxWidth(Double.MAX_VALUE);
        patientCombo.setCellFactory(lv -> patientCell());
        patientCombo.setButtonCell(patientCell());

        drugField.setPromptText("Nome del farmaco");
        dosageField.setPromptText("Es. 1 compressa");
        frequencyField.setPromptText("Es. Ogni 8 ore");

        submitBtn.getStyleClass().add("save-button");
    }

    public BorderPane buildRoot(Runnable onBack) {
        BorderPane root = buildShell("Crea Nuova Prescrizione", onBack);

        VBox content = new VBox(15);
        content.setPadding(new Insets(30, 50, 30, 50));
        content.setAlignment(Pos.TOP_CENTER);

        VBox formCard = new VBox(14);
        formCard.getStyleClass().add("info-card");
        formCard.setMaxWidth(600);
        formCard.setPadding(new Insets(25));

        Label titleForm = new Label("Dettagli Prescrizione");
        titleForm.getStyleClass().add("small-label");

        VBox boxPatient = createFieldBox("Paziente", patientCombo);
        VBox boxDrug = createFieldBox("Nome del Farmaco", drugField);
        VBox boxDosage = createFieldBox("Dosaggio", dosageField);
        VBox boxFreq = createFieldBox("Frequenza", frequencyField);

        HBox buttonBox = new HBox(submitBtn);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        formCard.getChildren().addAll(titleForm, boxPatient, boxDrug, boxDosage, boxFreq, buttonBox);
        content.getChildren().addAll(formCard, errorLabel);

        ScrollPane scroll = transparentScroll(content);
        root.setCenter(scroll);
        return root;
    }

    private VBox createFieldBox(String labelText, Control control) {
        VBox box = new VBox(6);
        Label lbl = new Label(labelText);
        lbl.getStyleClass().add("info-text");
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151;");
        control.getStyleClass().add(control instanceof ComboBox ? "combo-box" : "text-field");
        box.getChildren().addAll(lbl, control);
        return box;
    }

    private ListCell<PatientBean> patientCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(PatientBean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null :
                        item.getFullName() + " (" + item.getEmail() + ")");
            }
        };
    }
}