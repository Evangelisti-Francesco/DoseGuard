package it.ispwproject.doseguard.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class LoginGUIView {

    public final TextField      emailField            = new TextField();
    public final PasswordField  passwordField         = new PasswordField();
    public final TextField      visiblePasswordField  = new TextField();
    public final Label          errorLabel            = new Label("");
    public final Button         loginBtn              = new Button("Login");

    public LoginGUIView() {
        emailField.setPromptText("Inserisci email");
        emailField.setPrefWidth(250);
        emailField.setPrefHeight(48);

        passwordField.setPromptText("Inserisci password");
        passwordField.setPrefWidth(250);
        passwordField.setPrefHeight(48);

        visiblePasswordField.setPromptText("Inserisci password");
        visiblePasswordField.setPrefWidth(250);
        visiblePasswordField.setPrefHeight(48);
        visiblePasswordField.setVisible(false);

        visiblePasswordField.managedProperty().bind(visiblePasswordField.visibleProperty());
        passwordField.managedProperty().bind(passwordField.visibleProperty());
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        errorLabel.setWrapText(true);
        errorLabel.getStyleClass().add("error-label");

        loginBtn.setPrefWidth(110);
        loginBtn.setPrefHeight(42);
    }

    public HBox buildRoot(Runnable onLogin, Runnable onRegister) {
        HBox root = new HBox(75);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(25, 40, 25, 40));
        root.getStyleClass().add("doseguard-background");
        root.getChildren().addAll(buildLeftPanel(), buildRightPanel(onLogin, onRegister));
        return root;
    }

    private VBox buildLeftPanel() {
        VBox panel = new VBox(12);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(10));

        Label benvenuto = new Label("Benvenuto su");
        benvenuto.getStyleClass().add("title-label");

        // Caricamento del logo corretto dalla cartella icons
        ImageView logoView = new ImageView(
                new Image(getClass().getResourceAsStream("/icons/Brand_logo.png")));
        logoView.setFitWidth(150);
        logoView.setPreserveRatio(true);

        Label brand = new Label("DoseGuard");
        brand.getStyleClass().add("brand-label");

        Label tagline = new Label("La tua salute, sempre in orario!");
        tagline.getStyleClass().add("subtitle-label");

        panel.getChildren().addAll(benvenuto, logoView, brand, tagline);
        return panel;
    }

    private VBox buildRightPanel(Runnable onLogin, Runnable onRegister) {
        VBox panel = new VBox(14);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(20));
        panel.setMaxWidth(300);

        // Email
        Label emailLabel = fieldLabel("Email");
        emailField.setOnAction(e -> passwordField.requestFocus());
        VBox emailBox = new VBox(5, emailLabel, emailField);
        emailBox.setAlignment(Pos.CENTER_LEFT);

        // Password
        Label passwordLabel = fieldLabel("Password");
        passwordField.setOnAction(e -> onLogin.run());
        visiblePasswordField.setOnAction(e -> onLogin.run());

        StackPane passwordBox = new StackPane(passwordField, visiblePasswordField);

        CheckBox showPasswordCheck = new CheckBox("Mostra password");
        showPasswordCheck.selectedProperty().addListener((obs, oldVal, show) -> {
            visiblePasswordField.setVisible(show);
            passwordField.setVisible(!show);
        });

        VBox passwordContainer = new VBox(5, passwordLabel, passwordBox, showPasswordCheck);
        passwordContainer.setAlignment(Pos.CENTER_LEFT);

        // Bottone Login
        loginBtn.setOnAction(e -> onLogin.run());

        // Link Registrazione
        Label linkLabel = new Label("Non hai ancora un account?");
        linkLabel.getStyleClass().add("register-label");
        Hyperlink registerLink = new Hyperlink("Registrati qui");
        registerLink.setOnAction(e -> onRegister.run());
        VBox registerBox = new VBox(0, linkLabel, registerLink);
        registerBox.setAlignment(Pos.CENTER);

        panel.getChildren().addAll(emailBox, passwordContainer, errorLabel, loginBtn, registerBox);
        return panel;
    }

    public void setError(String message) {
        errorLabel.setText(message);
    }

    private Label fieldLabel(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("field-label");
        return lbl;
    }
}