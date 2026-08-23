package it.ispwproject.doseguard.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;

public class LoginGUIView {

    public final TextField emailField = new TextField();
    public final PasswordField passwordField = new PasswordField();
    public final Button loginBtn = new Button("Sign In");
    public final Button registerBtn = new Button("Forgot Password?");
    public final Label errorLabel = new Label();

    private static final String BRAND_BLUE = "#2551D8";
    private static final String IMAGE_PATH = "/images/Doctor_image.jpg";

    public void setError(String message) {
        errorLabel.setText(message);
    }

    public void clearError() {
        errorLabel.setText("");
    }

    public BorderPane buildRoot(Runnable onLogin, Runnable onRegister) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #DDE5ED;");

        loginBtn.setOnAction(e -> onLogin.run());
        registerBtn.setOnAction(e -> onRegister.run());

        HBox cardContainer = new HBox();
        cardContainer.setMaxWidth(900);
        cardContainer.setMaxHeight(520);
        cardContainer.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.12), 15, 0, 0, 6);");

        // ==========================================
        // COLONNA DI SINISTRA (BLU + IMMAGINE FULL WIDTH)
        // ==========================================
        VBox leftSide = new VBox(0);
        leftSide.setPrefWidth(320);
        leftSide.setMinWidth(320);
        leftSide.setMaxWidth(320);
        leftSide.setStyle("-fx-background-color: " + BRAND_BLUE + "; -fx-background-radius: 20 0 0 20;");

        ImageView doctorImage = new ImageView();
        doctorImage.setFitWidth(320);
        doctorImage.setFitHeight(200);
        doctorImage.setPreserveRatio(false);

        InputStream imgStream = getClass().getResourceAsStream(IMAGE_PATH);
        if (imgStream != null) {
            doctorImage.setImage(new Image(imgStream));

            // Clip arrotondato solo in alto a sinistra
            Rectangle clip = new Rectangle(320, 200);
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            doctorImage.setClip(clip);
        }

        VBox bannerBox = new VBox(12);
        bannerBox.setPadding(new Insets(20, 24, 20, 24));

        Label leftLogo = new Label("DoseGuard");
        leftLogo.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        VBox quoteBox = new VBox(4);
        quoteBox.setStyle("-fx-border-color: white; -fx-border-width: 0 0 0 3; -fx-padding: 0 0 0 12;");

        Label titleWelcome = new Label("Welcome to DoseGuard");
        titleWelcome.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label subText = new Label("Your health, perfectly scheduled.\nEvery dose, on time.");
        subText.setStyle("-fx-text-fill: #E0E7FF; -fx-font-size: 12px;");

        quoteBox.getChildren().addAll(titleWelcome, subText);
        bannerBox.getChildren().addAll(leftLogo, quoteBox);
        VBox.setVgrow(bannerBox, Priority.ALWAYS);

        leftSide.getChildren().addAll(doctorImage, bannerBox);

        // ==========================================
        // COLONNA DI DESTRA (FORM LOGIN)
        // ==========================================
        VBox rightSide = new VBox(12);
        rightSide.setPadding(new Insets(30, 40, 30, 40));
        rightSide.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(rightSide, Priority.ALWAYS);

        Label logoRight = new Label("DoseGuard");
        logoRight.setStyle("-fx-text-fill: " + BRAND_BLUE + "; -fx-font-size: 24px; -fx-font-weight: bold;");

        Label mainTitle = new Label("Login");
        mainTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        Label subtitle = new Label("Enter your credentials to access your health dashboard");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #6B7280;");

        Label emailLabel = new Label("Email");
        emailLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1F2937;");

        emailField.setPromptText("Email address");
        emailField.setPrefHeight(38);
        emailField.setMaxWidth(Double.MAX_VALUE);
        emailField.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 18; -fx-padding: 0 14; -fx-border-color: transparent;");

        Label passLabel = new Label("Password");
        passLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1F2937;");

        passwordField.setPromptText("••••••••••••••");
        passwordField.setPrefHeight(38);
        passwordField.setMaxWidth(Double.MAX_VALUE);
        passwordField.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 18; -fx-padding: 0 14; -fx-border-color: transparent;");

        registerBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + BRAND_BLUE + "; -fx-font-size: 12px; -fx-cursor: hand;");
        HBox forgotBox = new HBox(registerBtn);
        forgotBox.setAlignment(Pos.CENTER_RIGHT);

        loginBtn.setPrefHeight(42);
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle("-fx-background-color: " + BRAND_BLUE + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;");

        errorLabel.setTextFill(Color.RED);
        errorLabel.setStyle("-fx-font-size: 12px;");

        rightSide.getChildren().addAll(
                logoRight, mainTitle, subtitle,
                emailLabel, emailField,
                passLabel, passwordField,
                forgotBox,
                loginBtn, errorLabel
        );

        cardContainer.getChildren().addAll(leftSide, rightSide);

        StackPane centerContainer = new StackPane(cardContainer);
        centerContainer.setPadding(new Insets(24));
        root.setCenter(centerContainer);

        return root;
    }
}