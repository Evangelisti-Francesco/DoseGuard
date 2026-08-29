package it.ispwproject.doseguard.view.gui;

import it.ispwproject.doseguard.bean.MedicationBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

public class ViewMedicationsGUIView {

    public final Label pageTitle = new Label("I miei farmaci");
    public final Button goBackBtn = new Button("← Go Back");
    public final Label errorLabel = new Label("");

    private final VBox pendingContainer = new VBox(10);
    private final VBox takenContainer = new VBox(10);
    public final VBox mainContentBox = new VBox(20);

    public ViewMedicationsGUIView() {
        pageTitle.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2551D8;");
        errorLabel.setStyle("-fx-text-fill: #d9534f; -fx-font-size: 14px;");
        goBackBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2551D8; -fx-font-size: 16px; -fx-cursor: hand;");
    }

    public BorderPane buildRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #DDE5ED;");

        BorderPane mainContainer = new BorderPane();
        mainContainer.setPrefWidth(1100);
        mainContainer.setPrefHeight(740);
        mainContainer.setPadding(new Insets(30, 45, 30, 45));
        mainContainer.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 5);");

        VBox topSection = new VBox(20);
        BorderPane header = buildHeaderInsideCard();
        VBox titleBox = new VBox(5, pageTitle, errorLabel);
        topSection.getChildren().addAll(header, titleBox);
        mainContainer.setTop(topSection);

        mainContentBox.setAlignment(Pos.TOP_LEFT);
        mainContentBox.setPadding(new Insets(10, 5, 10, 5));

        ScrollPane scrollPane = new ScrollPane(mainContentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white; -fx-background-color: white; -fx-border-color: transparent;");
        mainContainer.setCenter(scrollPane);

        HBox footer = new HBox(goBackBtn);
        footer.setPadding(new Insets(10, 0, 0, 0));
        footer.setAlignment(Pos.CENTER_LEFT);
        mainContainer.setBottom(footer);

        StackPane outerCenterContainer = new StackPane(mainContainer);
        outerCenterContainer.setAlignment(Pos.CENTER);
        outerCenterContainer.setPadding(new Insets(20));
        root.setCenter(outerCenterContainer);

        return root;
    }

    private BorderPane buildHeaderInsideCard() {
        BorderPane header = new BorderPane();
        header.setPadding(new Insets(0, 0, 5, 0));
        header.setStyle("-fx-background-color: transparent;");

        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);

        InputStream logoStream = getClass().getResourceAsStream("/icons/Brand_logo.png");
        if (logoStream != null) {
            ImageView logo = new ImageView(new Image(logoStream, 28, 28, true, true));
            logoBox.getChildren().add(logo);
        }

        Label brand = new Label("DoseGuard");
        brand.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2551D8;");
        logoBox.getChildren().add(brand);

        InputStream profileStream = getClass().getResourceAsStream("/icons/Profile.png");
        ImageView profileImage = profileStream != null ? new ImageView(new Image(profileStream, 22, 22, true, true)) : new ImageView();

        Button profileBtn = new Button();
        profileBtn.setGraphic(profileImage);
        profileBtn.setShape(new javafx.scene.shape.Circle(18));
        profileBtn.setMinSize(38, 38);
        profileBtn.setMaxSize(38, 38);
        profileBtn.setStyle(
                "-fx-background-color: #E5E7EB; -fx-cursor: hand; " +
                        "-fx-background-radius: 19; -fx-border-color: #D1D5DB; -fx-border-radius: 19;"
        );

        header.setLeft(logoBox);
        header.setRight(profileBtn);
        return header;
    }

    public void renderMedications(List<MedicationBean> medications, Consumer<MedicationBean> onMarkAsTaken) {
        mainContentBox.getChildren().clear();

        if (medications.isEmpty()) {
            Label emptyLbl = new Label("Nessun farmaco o terapia in elenco.");
            emptyLbl.setStyle("-fx-font-size: 15px; -fx-text-fill: #6B7280; -fx-padding: 20 0;");
            mainContentBox.getChildren().add(emptyLbl);
            return;
        }

        Label pendingTitle = new Label("Da assumere");
        pendingTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1F2937; -fx-padding: 10 0 5 0;");
        pendingContainer.getChildren().clear();

        List<MedicationBean> pendingList = medications.stream().filter(m -> !m.isTaken()).toList();
        if (pendingList.isEmpty()) {
            Label noPendingLbl = new Label("Tutti i farmaci programmati sono stati già assunti!");
            noPendingLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #10B981; -fx-padding: 0 0 10 0;");
            pendingContainer.getChildren().add(noPendingLbl);
        } else {
            for (MedicationBean m : pendingList) {
                HBox card = createMedicationCard(m, true, onMarkAsTaken);
                pendingContainer.getChildren().add(card);
            }
        }

        Label takenTitle = new Label("Assunti");
        takenTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1F2937; -fx-padding: 15 0 5 0;");
        takenContainer.getChildren().clear();

        List<MedicationBean> takenList = medications.stream().filter(MedicationBean::isTaken).toList();
        if (takenList.isEmpty()) {
            Label noTakenLbl = new Label("Nessun farmaco già assunto.");
            noTakenLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280; -fx-padding: 0 0 10 0;");
            takenContainer.getChildren().add(noTakenLbl);
        } else {
            for (MedicationBean m : takenList) {
                HBox card = createMedicationCard(m, false, null);
                takenContainer.getChildren().add(card);
            }
        }

        mainContentBox.getChildren().addAll(pendingTitle, pendingContainer, takenTitle, takenContainer);
    }

    private HBox createMedicationCard(MedicationBean m, boolean isPending, Consumer<MedicationBean> onMarkAsTaken) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(12, 18, 12, 18));
        card.setStyle(
                "-fx-background-color: #F9FAFB; -fx-background-radius: 10; " +
                        "-fx-border-color: #E5E7EB; -fx-border-radius: 10;"
        );

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label nameLbl = new Label(m.getName());
        nameLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2551D8;");

        Label dosageLbl = new Label("Dosaggio: " + m.getDosage());
        dosageLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #4B5563;");

        info.getChildren().addAll(nameLbl, dosageLbl);
        card.getChildren().add(info);

        if (isPending && onMarkAsTaken != null) {
            Button takeBtn = new Button("Segna come assunto");
            takeBtn.setStyle(
                    "-fx-background-color: #d1fae5; -fx-text-fill: #059669; -fx-font-weight: bold; " +
                            "-fx-background-radius: 8; -fx-padding: 6 12; -fx-cursor: hand;"
            );
            takeBtn.setOnAction(e -> onMarkAsTaken.accept(m));
            card.getChildren().add(takeBtn);
        } else {
            Label doneLabel = new Label("✓ Assunto");
            doneLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #10B981;");
            card.getChildren().add(doneLabel);
        }

        return card;
    }

    public void setError(String msg) {
        errorLabel.setText(msg);
    }

    public void clearError() {
        errorLabel.setText("");
    }
}
