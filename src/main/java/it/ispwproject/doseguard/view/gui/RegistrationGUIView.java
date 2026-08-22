package it.ispwproject.doseguard.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class RegistrationGUIView {

    private static final double FORM_WIDTH           = 900;
    private static final double DOCTOR_SECTION_WIDTH = 820;

    // Campi esposti al controller
    public final TextField      nameField                   = new TextField();
    public final TextField      surnameField                = new TextField();
    public final TextField      fiscalCodeField             = new TextField();
    public final TextField      emailField                  = new TextField();
    public final PasswordField  passwordField               = new PasswordField();
    public final PasswordField  confirmPasswordField        = new PasswordField();
    public final TextField      visiblePasswordField        = new TextField();
    public final TextField      visibleConfirmPasswordField = new TextField();

    // Scelta Ruolo
    public final RadioButton    patientRadio                = new RadioButton("Paziente");
    public final RadioButton    doctorRadio                 = new RadioButton("Medico");
    public final RadioButton    pharmacistRadio             = new RadioButton("Farmacista");
    public final ComboBox<String> specializationComboBox    = new ComboBox<>();

    public final Label          errorLabel                  = new Label("");
    public final Button         registerBtn                 = new Button("Registrami");

    private VBox doctorSection;

    public RegistrationGUIView() {
        // Password bindings
        visiblePasswordField.setVisible(false);
        visibleConfirmPasswordField.setVisible(false);
        visiblePasswordField.managedProperty().bind(visiblePasswordField.visibleProperty());
        visibleConfirmPasswordField.managedProperty().bind(visibleConfirmPasswordField.visibleProperty());
        passwordField.managedProperty().bind(passwordField.visibleProperty());
        confirmPasswordField.managedProperty().bind(confirmPasswordField.visibleProperty());
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());
        visibleConfirmPasswordField.textProperty().bindBidirectional(confirmPasswordField.textProperty());

        errorLabel.setWrapText(true);
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setMaxWidth(FORM_WIDTH);

        registerBtn.getStyleClass().add("button");
        registerBtn.setPrefWidth(140);
        registerBtn.setPrefHeight(38);

        // Opzioni di specializzazione per i medici
        specializationComboBox.getItems().addAll(
                "Medicina Generale",
                "Cardiologia",
                "Neurologia",
                "Pediatria"
        );
        specializationComboBox.getSelectionModel().selectFirst();
        specializationComboBox.setPrefWidth(270);
    }

    public ScrollPane buildRoot(Runnable onBack) {
        doctorSection = buildDoctorSection();
        doctorSection.setVisible(false);
        doctorSection.setManaged(false);

        bindRoleRadios();

        VBox root = new VBox(14);
        root.setPadding(new Insets(18, 60, 24, 60));
        root.setAlignment(Pos.TOP_CENTER);
        root.getStyleClass().add("doseguard-background");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().addAll("transparent-scroll");
        scrollPane.viewportBoundsProperty().addListener((obs, o, n) ->
                root.setMinHeight(n.getHeight()));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMaxWidth(FORM_WIDTH);
        Button backBtn = new Button("‹ Indietro");
        backBtn.getStyleClass().add("back-button");
        backBtn.setOnAction(e -> onBack.run());
        header.getChildren().add(backBtn);

        Label title = new Label("Registrazione DoseGuard");
        title.getStyleClass().add("title-label");

        ImageView logoView = new ImageView();
        var logoStream = getClass().getResourceAsStream("/icons/Brand_logo.png");
        if (logoStream != null) {
            logoView.setImage(new Image(logoStream, 80, 80, true, true));
            logoView.setFitHeight(58);
            logoView.setFitWidth(58);
            logoView.setPreserveRatio(true);
            logoView.setSmooth(true);
        }

        GridPane form = buildForm();

        root.getChildren().addAll(header, title, logoView, form, doctorSection, errorLabel, registerBtn);
        scrollPane.setContent(root);
        return scrollPane;
    }

    public void setError(String message) {
        errorLabel.setText(message);
    }

    private GridPane buildForm() {
        GridPane grid = new GridPane();
        grid.setHgap(34);
        grid.setVgap(8);
        grid.setPrefWidth(FORM_WIDTH);
        grid.setMaxWidth(FORM_WIDTH);
        grid.setAlignment(Pos.CENTER);

        nameField.setPromptText("inserisci nome");               nameField.setPrefWidth(270);       nameField.setPrefHeight(30);
        surnameField.setPromptText("inserisci cognome");         surnameField.setPrefWidth(270);    surnameField.setPrefHeight(30);
        fiscalCodeField.setPromptText("codice fiscale");         fiscalCodeField.setPrefWidth(270); fiscalCodeField.setPrefHeight(30);
        emailField.setPromptText("inserisci email");             emailField.setPrefWidth(270);      emailField.setPrefHeight(30);

        passwordField.setPromptText("inserisci password");       passwordField.setPrefWidth(320);        passwordField.setPrefHeight(30);
        confirmPasswordField.setPromptText("ripeti password");   confirmPasswordField.setPrefWidth(320); confirmPasswordField.setPrefHeight(30);
        visiblePasswordField.setPromptText("inserisci password"); visiblePasswordField.setPrefWidth(320); visiblePasswordField.setPrefHeight(30);
        visibleConfirmPasswordField.setPromptText("ripeti password"); visibleConfirmPasswordField.setPrefWidth(320); visibleConfirmPasswordField.setPrefHeight(30);

        VBox leftColumn = new VBox(6,
                fieldBlock("Nome *",           nameField),
                fieldBlock("Cognome *",        surnameField),
                fieldBlock("Codice Fiscale *", fiscalCodeField),
                fieldBlock("Email *",          emailField),
                requiredLabel());

        VBox centerColumn = new VBox(12);
        centerColumn.setAlignment(Pos.TOP_LEFT);
        centerColumn.getChildren().addAll(fieldLabel("Ruolo *"), buildRoleBox());

        CheckBox showPasswords = new CheckBox("Mostra password");
        showPasswords.selectedProperty().addListener((obs, o, show) -> {
            visiblePasswordField.setVisible(show);
            passwordField.setVisible(!show);
            visibleConfirmPasswordField.setVisible(show);
            confirmPasswordField.setVisible(!show);
        });
        HBox showBox = new HBox(showPasswords);
        showBox.setAlignment(Pos.CENTER_RIGHT);

        VBox rightColumn = new VBox(6,
                passwordBlock(), passwordRules(), confirmPasswordBlock(), showBox);

        grid.add(leftColumn,   0, 0);
        grid.add(centerColumn, 1, 0);
        grid.add(rightColumn,  2, 0);

        ColumnConstraints c1 = new ColumnConstraints(); c1.setPrefWidth(270);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPrefWidth(180);
        ColumnConstraints c3 = new ColumnConstraints(); c3.setPrefWidth(320);
        grid.getColumnConstraints().addAll(c1, c2, c3);
        return grid;
    }

    private void bindRoleRadios() {
        doctorRadio.setOnAction(e -> {
            doctorSection.setVisible(true);
            doctorSection.setManaged(true);
        });
        patientRadio.setOnAction(e -> {
            doctorSection.setVisible(false);
            doctorSection.setManaged(false);
        });
        pharmacistRadio.setOnAction(e -> {
            doctorSection.setVisible(false);
            doctorSection.setManaged(false);
        });
    }

    private VBox buildRoleBox() {
        ToggleGroup roleGroup = new ToggleGroup();
        patientRadio.setToggleGroup(roleGroup);
        patientRadio.setSelected(true);
        doctorRadio.setToggleGroup(roleGroup);
        pharmacistRadio.setToggleGroup(roleGroup);

        VBox box = new VBox(8, patientRadio, doctorRadio, pharmacistRadio);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private VBox buildDoctorSection() {
        VBox section = new VBox(8);
        section.setAlignment(Pos.TOP_LEFT);
        section.setPrefWidth(DOCTOR_SECTION_WIDTH);
        section.setMaxWidth(DOCTOR_SECTION_WIDTH);
        section.setPadding(new Insets(8, 0, 0, 0));
        section.getChildren().addAll(
                fieldLabel("Specializzazione Medica *"), specializationComboBox);
        return section;
    }

    private VBox fieldBlock(String labelText, Control field) {
        return new VBox(3, fieldLabel(labelText), field);
    }

    private Label fieldLabel(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("small-label");
        return lbl;
    }

    private Label requiredLabel() {
        Label lbl = new Label("* campi obbligatori");
        lbl.getStyleClass().add("register-label");
        return lbl;
    }

    private Label passwordRules() {
        Label lbl = new Label("""
                La tua password deve includere:
                • almeno 8 caratteri
                • una lettera maiuscola
                • almeno un numero""");
        lbl.getStyleClass().add("register-label");
        lbl.setWrapText(true);
        lbl.setMaxWidth(300);
        return lbl;
    }

    private VBox passwordBlock() {
        return new VBox(3, fieldLabel("Password *"),
                new StackPane(passwordField, visiblePasswordField));
    }

    private VBox confirmPasswordBlock() {
        return new VBox(3, fieldLabel("Conferma Password *"),
                new StackPane(confirmPasswordField, visibleConfirmPasswordField));
    }
}
