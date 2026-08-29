package it.ispwproject.doseguard.view.gui;

import it.ispwproject.doseguard.bean.PrescriptionBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.InputStream;
import java.util.List;

public class ViewPrescriptionsGUIView {

    public final Label pageTitle = new Label("Prescrizioni");
    public final Button goBackBtn = new Button("← Go Back");
    public final Label errorLabel = new Label("");

    // Elementi dedicati a Medico / Farmacista (Ricerca per Codice Fiscale)
    public final VBox searchSectionBox = new VBox(10);
    public final TextField fiscalCodeField = new TextField();
    public final Button searchBtn = new Button("Cerca Prescrizioni");

    // Contenitore della lista prescrizioni
    public final VBox prescriptionsListContainer = new VBox(12);

    public ViewPrescriptionsGUIView() {
        pageTitle.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2551D8;");
        errorLabel.setStyle("-fx-text-fill: #d9534f; -fx-font-size: 14px;");
        goBackBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2551D8; -fx-font-size: 16px; -fx-cursor: hand;");

        fiscalCodeField.setPromptText("Inserisci il codice fiscale del paziente");
        fiscalCodeField.setMaxWidth(350);
        fiscalCodeField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #D1D5DB; -fx-padding: 8;");

        searchBtn.setStyle(
                "-fx-background-color: #2551D8; -fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand;"
        );
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

        searchSectionBox.getChildren().clear();
        Label searchLbl = new Label("Ricerca Paziente");
        searchLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #374151;");

        HBox searchInputRow = new HBox(10, fiscalCodeField, searchBtn);
        searchInputRow.setAlignment(Pos.CENTER_LEFT);
        searchSectionBox.getChildren().addAll(searchLbl, searchInputRow);

        VBox titleBox = new VBox(5, pageTitle, errorLabel, searchSectionBox);
        topSection.getChildren().addAll(header, titleBox);
        mainContainer.setTop(topSection);

        prescriptionsListContainer.setAlignment(Pos.TOP_LEFT);
        prescriptionsListContainer.setPadding(new Insets(10, 5, 10, 5));

        ScrollPane scrollPane = new ScrollPane(prescriptionsListContainer);
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

    public void renderPrescriptions(List<PrescriptionBean> prescriptions) {
        prescriptionsListContainer.getChildren().clear();

        if (prescriptions.isEmpty()) {
            Label emptyLbl = new Label("Nessuna prescrizione trovata.");
            emptyLbl.setStyle("-fx-font-size: 15px; -fx-text-fill: #6B7280; -fx-padding: 20 0;");
            prescriptionsListContainer.getChildren().add(emptyLbl);
            return;
        }

        for (PrescriptionBean p : prescriptions) {
            VBox card = new VBox(6);
            card.setPadding(new Insets(15, 20, 15, 20));
            card.setStyle(
                    "-fx-background-color: #F9FAFB; -fx-background-radius: 12; " +
                            "-fx-border-color: #E5E7EB; -fx-border-radius: 12;"
            );

            Label drugLbl = new Label("Farmaco: " + p.getDrug());
            drugLbl.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #2551D8;");

            Label dosageLbl = new Label("Dosaggio: " + p.getDosage() + "  |  Frequenza: " + p.getFrequency());
            dosageLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #374151;");

            Label dateLbl = new Label("Data emissione: " + p.getIssueDate());
            dateLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #4B5563;");

            card.getChildren().addAll(drugLbl, dosageLbl, dateLbl);

            if (p.getDoctorFullName() != null && !p.getDoctorFullName().isBlank()) {
                Label docLbl = new Label("Medico prescrivente: Dr. " + p.getDoctorFullName());
                docLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280;");
                card.getChildren().add(docLbl);
            }

            prescriptionsListContainer.getChildren().add(card);
        }
    }

    public void setError(String msg) {
        errorLabel.setText(msg);
    }

    public void clearError() {
        errorLabel.setText("");
    }
}