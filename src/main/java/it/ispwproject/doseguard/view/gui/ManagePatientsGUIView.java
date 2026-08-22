package it.ispwproject.doseguard.view.gui;

import it.ispwproject.doseguard.bean.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class ManagePatientsGUIView extends PageGUIView {

    public final ComboBox<PatientBean> patientCombo = new ComboBox<>();
    public final Label                 errorLabel   = buildErrorLabel();

    public ManagePatientsGUIView() {
        patientCombo.getStyleClass().add("combo-box");
        patientCombo.setPromptText("Cerca paziente...");
        patientCombo.setMaxWidth(Double.MAX_VALUE);
        patientCombo.setCellFactory(lv -> patientCell());
        patientCombo.setButtonCell(patientCell());
    }

    public BorderPane buildRoot(Runnable onBack) {
        BorderPane root = buildShell("Gestisci Pazienti", onBack);

        VBox content = new VBox(12);
        content.setPadding(new Insets(28, 48, 28, 48));
        content.setAlignment(Pos.TOP_CENTER);

        VBox selectorCard = new VBox(10);
        selectorCard.getStyleClass().add("info-card");
        selectorCard.setMaxWidth(720);
        selectorCard.setAlignment(Pos.CENTER_LEFT);

        Label patientLabel = new Label("Seleziona paziente");
        patientLabel.getStyleClass().add("small-label");

        selectorCard.getChildren().addAll(patientLabel, patientCombo);

        VBox patientCard = new VBox(8);
        patientCard.setMaxWidth(720);
        patientCard.setVisible(false);
        patientCard.setManaged(false);

        patientCombo.setUserData(patientCard);

        content.getChildren().addAll(selectorCard, patientCard, errorLabel);

        ScrollPane scroll = transparentScroll(content);
        root.setCenter(scroll);
        return root;
    }

    public VBox getPatientCard() {
        return (VBox) patientCombo.getUserData();
    }

    public void buildPatientCard(VBox card, PatientBean patient,
                                 PatientProgressBean progress,
                                 List<TimeSlotBean> upcoming,
                                 List<TimeSlotBean> completed,
                                 Consumer<String> onSaveProgress) {
        card.getChildren().clear();
        card.setVisible(true);
        card.setManaged(true);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // ── Header paziente ───────────────────────────────────────────────
        HBox patientHeader = new HBox(12);
        patientHeader.setAlignment(Pos.CENTER_LEFT);
        patientHeader.getStyleClass().add("info-card");
        patientHeader.setMaxWidth(720);
        patientHeader.setPadding(new Insets(12, 16, 12, 16));

        Label avatar = new Label(
                String.valueOf(patient.getName().charAt(0)).toUpperCase() +
                        String.valueOf(patient.getSurname().charAt(0)).toUpperCase());
        avatar.setStyle("-fx-background-color: #8EADC2; -fx-background-radius: 20; " +
                "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; " +
                "-fx-min-width: 40; -fx-min-height: 40; -fx-alignment: center;");

        VBox patientInfo = new VBox(4);
        Label nameLabel  = new Label(patient.getFullName()); nameLabel.getStyleClass().add("welcome-label");
        Label emailLabel = new Label(patient.getEmail());    emailLabel.getStyleClass().add("info-text");
        patientInfo.getChildren().addAll(nameLabel, emailLabel);

        if (!upcoming.isEmpty()) {
            TimeSlotBean next = upcoming.get(0);
            Label nextVisit = new Label("📅  Prossima visita: " +
                    next.getDate().format(fmt) + "  ore " + next.getStartTime());
            nextVisit.getStyleClass().add("info-text");
            nextVisit.setStyle("-fx-text-fill: #5a8a6a; -fx-font-weight: bold;");
            patientInfo.getChildren().add(nextVisit);
        }

        if (!completed.isEmpty()) {
            Label storicoBtn = new Label("📖  Visite effettuate (" + completed.size() + ")  ▼");
            storicoBtn.getStyleClass().add("info-text");
            storicoBtn.setStyle("-fx-text-fill: #888; -fx-cursor: hand;");

            VBox storicoContent = new VBox(4);
            storicoContent.setVisible(false);
            storicoContent.setManaged(false);
            storicoContent.setPadding(new Insets(4, 0, 0, 8));

            for (TimeSlotBean b : completed) {
                Label l = new Label("• " + b.getDate().format(fmt) + "  ore " + b.getStartTime());
                l.getStyleClass().add("info-text");
                l.setStyle("-fx-text-fill: #888;");
                storicoContent.getChildren().add(l);
            }

            storicoBtn.setOnMouseClicked(e -> {
                boolean show = !storicoContent.isVisible();
                storicoContent.setVisible(show);
                storicoContent.setManaged(show);
                storicoBtn.setText("📖  Visite effettuate (" + completed.size() + ")  " + (show ? "▲" : "▼"));
            });
            patientInfo.getChildren().addAll(storicoBtn, storicoContent);
        }

        patientHeader.getChildren().addAll(avatar, patientInfo);

        // ── Note / Quadro clinico ─────────────────────────────────────────
        VBox progressBox = new VBox(12);
        progressBox.getStyleClass().add("info-card");
        progressBox.setMaxWidth(720);
        progressBox.setMaxHeight(Region.USE_PREF_SIZE);

        Label progressTitle = new Label("📝  Note e Quadro Clinico");
        progressTitle.getStyleClass().add("small-label");

        TextArea notesArea = new TextArea();
        notesArea.getStyleClass().add("text-area");
        notesArea.setPrefRowCount(5);
        notesArea.setWrapText(true);
        notesArea.setPromptText("Inserisci note relative al paziente...");
        if (progress != null) notesArea.setText(progress.getNotes());

        HBox progressFooter = new HBox(12);
        progressFooter.setAlignment(Pos.CENTER_LEFT);

        Label lastUpdate = new Label(progress != null && progress.getUpdatedAt() != null
                ? "Aggiornato il " + progress.getUpdatedAt().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy 'alle' HH:mm"))
                : "Nessun aggiornamento");
        lastUpdate.getStyleClass().add("info-text");
        lastUpdate.setStyle("-fx-text-fill: #999;");
        HBox.setHgrow(lastUpdate, Priority.ALWAYS);

        Button updateBtn = new Button("Salva");
        updateBtn.getStyleClass().add("save-button");
        updateBtn.setPrefWidth(100);
        updateBtn.setOnAction(e -> onSaveProgress.accept(notesArea.getText()));

        progressFooter.getChildren().addAll(lastUpdate, updateBtn);
        progressBox.getChildren().addAll(progressTitle, notesArea, progressFooter);

        card.getChildren().addAll(patientHeader, progressBox);
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
