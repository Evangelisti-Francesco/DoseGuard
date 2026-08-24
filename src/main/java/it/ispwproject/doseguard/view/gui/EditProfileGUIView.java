package it.ispwproject.doseguard.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.InputStream;

public class EditProfileGUIView {

    public final Label pageTitle = new Label("Modifica Profilo");
    public final Button goBackBtn = new Button("← Go Back");
    public final Label errorLabel = new Label("");
    public final Label successLabel = new Label("");

    // Campi informativi non modificabili
    public final Label nameValueLabel = new Label();
    public final Label surnameValueLabel = new Label();

    // Campo di modifica email
    public final TextField emailField = new TextField();
    public final Button updateEmailBtn = new Button("Aggiorna Email");

    public EditProfileGUIView() {
        pageTitle.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2551D8;");
        errorLabel.setStyle("-fx-text-fill: #d9534f; -fx-font-size: 14px;");
        successLabel.setStyle("-fx-text-fill: #10B981; -fx-font-size: 14px; -fx-font-weight: bold;");
        goBackBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2551D8; -fx-font-size: 16px; -fx-cursor: hand;");

        String fieldStyle = "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #D1D5DB; -fx-padding: 8;";
        emailField.setStyle(fieldStyle);

        String btnStyle =
                "-fx-background-color: #2551D8; -fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand;";
        updateEmailBtn.setStyle(btnStyle);
    }

    public BorderPane buildRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #DDE5ED;");

        BorderPane mainContainer = new BorderPane();
        mainContainer.setPrefWidth(1100);
        mainContainer.setPrefHeight(740);
        mainContainer.setPadding(new Insets(30, 45, 30, 45));
        mainContainer.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 5);");

        // TOP SECTION
        VBox topSection = new VBox(15);
        BorderPane header = buildHeaderInsideCard();
        VBox titleBox = new VBox(5, pageTitle, errorLabel, successLabel);
        topSection.getChildren().addAll(header, titleBox);
        mainContainer.setTop(topSection);

        // CENTER SECTION
        VBox formBox = new VBox(20);
        formBox.setAlignment(Pos.TOP_LEFT);
        formBox.setPadding(new Insets(15, 5, 15, 5));
        formBox.setMaxWidth(600);

        // Box Dati Personali (Sola lettura)
        VBox infoBox = new VBox(8);
        infoBox.setPadding(new Insets(15));
        infoBox.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 12; -fx-border-color: #E5E7EB; -fx-border-radius: 12;");

        Label infoTitle = new Label("Informazioni personali");
        infoTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1F2937;");

        HBox nameRow = new HBox(10, new Label("Nome:"), nameValueLabel);
        nameValueLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4B5563;");

        HBox surnameRow = new HBox(10, new Label("Cognome:"), surnameValueLabel);
        surnameValueLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4B5563;");

        infoBox.getChildren().addAll(infoTitle, nameRow, surnameRow);

        // Box Modifica Email
        VBox emailBox = new VBox(8);
        emailBox.setPadding(new Insets(15));
        emailBox.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 12; -fx-border-color: #E5E7EB; -fx-border-radius: 12;");

        Label emailTitle = new Label("Modifica Email");
        emailTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1F2937;");

        HBox emailActionRow = new HBox(10, emailField, updateEmailBtn);
        emailActionRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(emailField, Priority.ALWAYS);

        emailBox.getChildren().addAll(emailTitle, emailActionRow);

        formBox.getChildren().addAll(infoBox, emailBox);

        ScrollPane scrollPane = new ScrollPane(formBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white; -fx-background-color: white; -fx-border-color: transparent;");
        mainContainer.setCenter(scrollPane);

        // BOTTOM SECTION
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

    public void setSuccess(String msg) {
        successLabel.setText(msg);
        errorLabel.setText("");
    }

    public void setError(String msg) {
        errorLabel.setText(msg);
        successLabel.setText("");
    }
}
