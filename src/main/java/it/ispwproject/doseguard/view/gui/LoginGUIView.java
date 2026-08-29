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
    public final Hyperlink signupLink = new Hyperlink("Sign up");
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
        signupLink.setOnAction(e -> onRegister.run());

        HBox cardContainer = new HBox();
        cardContainer.setMaxWidth(1100);
        cardContainer.setMaxHeight(640);
        cardContainer.setStyle("-fx-background-color: white; -fx-background-radius: 24; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 20, 0, 0, 8);");


        VBox leftSide = new VBox(0);
        leftSide.setPrefWidth(400);
        leftSide.setMinWidth(400);
        leftSide.setMaxWidth(400);
        leftSide.setStyle("-fx-background-color: " + BRAND_BLUE + "; -fx-background-radius: 24 0 0 24;");

        ImageView doctorImage = new ImageView();
        doctorImage.setFitWidth(400);
        doctorImage.setFitHeight(250);
        doctorImage.setPreserveRatio(false);

        InputStream imgStream = getClass().getResourceAsStream(IMAGE_PATH);
        if (imgStream != null) {
            doctorImage.setImage(new Image(imgStream));

            Rectangle clip = new Rectangle(400, 250);
            clip.setArcWidth(24);
            clip.setArcHeight(24);
            doctorImage.setClip(clip);
        }

        VBox bannerBox = new VBox(16);
        bannerBox.setPadding(new Insets(25, 30, 25, 30));

        Label leftLogo = new Label("DoseGuard");
        leftLogo.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");

        VBox quoteBox = new VBox(6);
        quoteBox.setStyle("-fx-border-color: white; -fx-border-width: 0 0 0 4; -fx-padding: 0 0 0 16;");

        Label titleWelcome = new Label("Welcome to DoseGuard");
        titleWelcome.setStyle("-fx-text-fill: white; -fx-font-size: 17px; -fx-font-weight: bold;");

        Label subText = new Label("Your health, perfectly scheduled.\nEvery dose, on time.");
        subText.setStyle("-fx-text-fill: #E0E7FF; -fx-font-size: 14px;");

        quoteBox.getChildren().addAll(titleWelcome, subText);
        bannerBox.getChildren().addAll(leftLogo, quoteBox);
        VBox.setVgrow(bannerBox, Priority.ALWAYS);

        leftSide.getChildren().addAll(doctorImage, bannerBox);


        VBox rightSide = new VBox(12);
        rightSide.setPadding(new Insets(30, 50, 30, 50));
        rightSide.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(rightSide, Priority.ALWAYS);

        Label logoRight = new Label("DoseGuard");
        logoRight.setStyle("-fx-text-fill: " + BRAND_BLUE + "; -fx-font-size: 30px; -fx-font-weight: bold;");

        Label mainTitle = new Label("Login");
        mainTitle.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        Label subtitle = new Label("Enter your credentials to access your health dashboard");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280;");

        Label emailLabel = new Label("Email");
        emailLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1F2937;");

        emailField.setPromptText("Email address");
        emailField.setPrefHeight(44);
        emailField.setMaxWidth(Double.MAX_VALUE);
        emailField.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 22; -fx-padding: 0 16; -fx-font-size: 14px; -fx-border-color: transparent;");

        Label passLabel = new Label("Password");
        passLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1F2937;");

        passwordField.setPromptText("••••••••••••••");
        passwordField.setPrefHeight(44);
        passwordField.setMaxWidth(Double.MAX_VALUE);
        passwordField.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 22; -fx-padding: 0 16; -fx-font-size: 14px; -fx-border-color: transparent;");

        registerBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + BRAND_BLUE + "; -fx-font-size: 13px; -fx-cursor: hand;");
        HBox forgotBox = new HBox(registerBtn);
        forgotBox.setAlignment(Pos.CENTER_RIGHT);

        loginBtn.setPrefHeight(46);
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle("-fx-background-color: " + BRAND_BLUE + "; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 23; -fx-cursor: hand;");

        errorLabel.setTextFill(Color.RED);
        errorLabel.setStyle("-fx-font-size: 13px;");

        Label noAccountLabel = new Label("Don't have an account?");
        noAccountLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6B7280;");

        signupLink.setStyle("-fx-font-size: 14px; -fx-text-fill: " + BRAND_BLUE + "; -fx-font-weight: bold; -fx-cursor: hand;");

        HBox signupBox = new HBox(6, noAccountLabel, signupLink);
        signupBox.setAlignment(Pos.CENTER);
        signupBox.setPadding(new Insets(6, 0, 0, 0));

        rightSide.getChildren().addAll(
                logoRight, mainTitle, subtitle,
                emailLabel, emailField,
                passLabel, passwordField,
                forgotBox,
                loginBtn, errorLabel,
                signupBox
        );

        cardContainer.getChildren().addAll(leftSide, rightSide);

        StackPane centerContainer = new StackPane(cardContainer);
        centerContainer.setPadding(new Insets(20));
        root.setCenter(centerContainer);

        return root;
    }
}