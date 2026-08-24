package it.ispwproject.doseguard.view.gui;

import it.ispwproject.doseguard.bean.DoctorBean;
import it.ispwproject.doseguard.bean.SpecializationBean;
import it.ispwproject.doseguard.bean.TimeSlotBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class BookAppointmentGUIView {

    public final Label pageTitle = new Label("Select medical speciality");
    public final HBox cardsContainer = new HBox(15);
    public final Button goBackBtn = new Button("← Go Back");
    public final Label errorLabel = new Label("");

    public BookAppointmentGUIView() {
        pageTitle.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2551D8;");

        cardsContainer.setAlignment(Pos.CENTER);
        cardsContainer.setPadding(new Insets(10, 5, 10, 5));

        goBackBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2551D8; -fx-font-size: 16px; -fx-cursor: hand;");
        errorLabel.setStyle("-fx-text-fill: #d9534f; -fx-font-size: 14px;");
    }

    public BorderPane buildRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #DDE5ED;");

        // Contenitore bianco centrale principale impostato come BorderPane interno
        BorderPane mainContainer = new BorderPane();
        mainContainer.setPrefWidth(1100);
        mainContainer.setPrefHeight(740);
        mainContainer.setPadding(new Insets(30, 45, 30, 45));
        mainContainer.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 5);");

        // 1. TOP: Header (Logo, Nome e Profilo) + Titolo ed Errori raggruppati insieme
        VBox topSection = new VBox(20);
        BorderPane header = buildHeaderInsideCard();
        VBox titleBox = new VBox(5, pageTitle, errorLabel);
        topSection.getChildren().addAll(header, titleBox);
        mainContainer.setTop(topSection);

        // 2. CENTER: Le card (si posizioneranno al centro esatto dello spazio rimanente)
        StackPane centerWrapper = new StackPane(cardsContainer);
        centerWrapper.setAlignment(Pos.CENTER);
        mainContainer.setCenter(centerWrapper);

        // 3. BOTTOM: Il tasto Go Back sempre in basso fisso
        HBox footer = buildFooterInsideCard();
        mainContainer.setBottom(footer);

        // Centriamo il riquadro bianco nella finestra
        StackPane outerCenterContainer = new StackPane(mainContainer);
        outerCenterContainer.setAlignment(Pos.CENTER);
        outerCenterContainer.setPadding(new Insets(20));
        root.setCenter(outerCenterContainer);

        return root;
    }

    private BorderPane buildHeaderInsideCard() {
        BorderPane header = new BorderPane();
        header.setPadding(new Insets(0, 0, 10, 0));
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

    private HBox buildFooterInsideCard() {
        HBox footer = new HBox(goBackBtn);
        footer.setPadding(new Insets(10, 0, 0, 0));
        footer.setAlignment(Pos.CENTER_LEFT);
        return footer;
    }

    // ── STEP 1: CARD SPECIALIZZAZIONI ──────────────────────────────────────────
    public void showSpecializations(List<SpecializationBean> specs, Consumer<SpecializationBean> onSelect) {
        pageTitle.setText("Select medical speciality");
        cardsContainer.getChildren().clear();

        for (SpecializationBean spec : specs) {
            VBox card = createBaseCard();

            String specNameLower = spec.getName().toLowerCase();
            String iconName;
            String descriptionText;

            if (specNameLower.contains("cardio")) {
                iconName = "Cardiology.png";
                descriptionText = "System filters specialized entities for heart-related treatments.";
            } else if (specNameLower.contains("neuro")) {
                iconName = "Neurology.png";
                descriptionText = "System retrieves neurological specialists and availability.";
            } else {
                iconName = "Internal Med.png";
                descriptionText = "System shows general practitioners for primary care.";
            }

            InputStream stream = getClass().getResourceAsStream("/icons/" + iconName);
            ImageView icon = stream != null ? new ImageView(new Image(stream, 50, 50, true, true)) : new ImageView();

            Label name = new Label(spec.getName());
            name.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2551D8;");

            Label description = new Label(descriptionText);
            styleDescriptionLabel(description);

            card.getChildren().addAll(icon, name, description);
            card.setOnMouseClicked(e -> onSelect.accept(spec));
            cardsContainer.getChildren().add(card);
        }
    }

    // ── STEP 2: CARD MEDICI ────────────────────────────────────────────────────
    public void showDoctors(List<DoctorBean> doctors, Consumer<DoctorBean> onSelect) {
        pageTitle.setText("Select a doctor");
        cardsContainer.getChildren().clear();

        if (doctors.isEmpty()) {
            cardsContainer.getChildren().add(new Label("No doctors available for this speciality."));
            return;
        }

        for (DoctorBean doc : doctors) {
            VBox card = createBaseCard();

            String docNameLower = doc.getFullName().toLowerCase();
            String doctorImgPath;

            if (docNameLower.contains("neri")) {
                doctorImgPath = "/images/Doctor Neri.png";
            } else if (docNameLower.contains("rossi")) {
                doctorImgPath = "/images/Doctor Rossi.png";
            }else if (docNameLower.contains("gialli")) {
                doctorImgPath = "/images/Doctor Gialli.jpg";
            } else {
                doctorImgPath = "/images/General Doctor.png";
            }

            InputStream stream = getClass().getResourceAsStream(doctorImgPath);

            // Contenitore circolare per l'avatar
            StackPane imageWrapper = new StackPane();
            imageWrapper.setPrefSize(90, 90);
            imageWrapper.setMinSize(90, 90);
            imageWrapper.setMaxSize(90, 90);

            // Maschera circolare (diametro 90 -> raggio 45)
            javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(45, 45, 45);
            imageWrapper.setClip(clip);

            if (stream != null) {
                ImageView photo = new ImageView(new Image(stream));
                photo.setPreserveRatio(true);
                photo.fitWidthProperty().bind(imageWrapper.widthProperty());
                photo.fitHeightProperty().bind(imageWrapper.heightProperty());

                // Forza il posizionamento al centro esatto
                StackPane.setAlignment(photo, Pos.CENTER);
                imageWrapper.getChildren().add(photo);
            }

            Label name = new Label("Dr. " + doc.getSurname());
            name.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2551D8;");

            Label details = new Label("Dott: " + doc.getFullName());
            styleDescriptionLabel(details);

            card.getChildren().addAll(imageWrapper, name, details);
            card.setOnMouseClicked(e -> onSelect.accept(doc));
            cardsContainer.getChildren().add(card);
        }
    }

    // ── STEP 3: CARD ORARI ─────────────────────────────────────────────────────
    public void showTimeSlots(List<TimeSlotBean> slots, Consumer<TimeSlotBean> onSelect) {
        pageTitle.setText("Select a time slot");
        cardsContainer.getChildren().clear();

        if (slots.isEmpty()) {
            cardsContainer.getChildren().add(new Label("No available time slots for this doctor."));
            return;
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (TimeSlotBean slot : slots) {
            VBox card = createBaseCard();

            InputStream stream = getClass().getResourceAsStream("/icons/Calendar.png");
            ImageView icon = stream != null ? new ImageView(new Image(stream, 50, 50, true, true)) : new ImageView();

            Label dateLbl = new Label(slot.getDate().format(fmt));
            dateLbl.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #2551D8;");

            Label timeLbl = new Label("Ore: " + slot.getStartTime());
            styleDescriptionLabel(timeLbl);

            card.getChildren().addAll(icon, dateLbl, timeLbl);
            card.setOnMouseClicked(e -> onSelect.accept(slot));
            cardsContainer.getChildren().add(card);
        }
    }

    public void setError(String msg) {
        errorLabel.setText(msg);
    }

    public void clearError() {
        errorLabel.setText("");
    }

    private VBox createBaseCard() {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(315, 215);
        card.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 14; -fx-padding: 15; -fx-cursor: hand;");
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #D1D5DB; -fx-background-radius: 14; -fx-padding: 15; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 14; -fx-padding: 15; -fx-cursor: hand;"));
        return card;
    }

    private void styleDescriptionLabel(Label lbl) {
        lbl.setStyle("-fx-font-size: 11.5px; -fx-text-fill: #6B7280;");
        lbl.setAlignment(Pos.CENTER);
        lbl.setWrapText(true);
    }
}