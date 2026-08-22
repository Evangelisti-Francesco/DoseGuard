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
    public final HBox cardsContainer = new HBox(25);
    public final Button goBackBtn = new Button("← Go Back");
    public final Label errorLabel = new Label("");

    public BookAppointmentGUIView() {
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2B4C7E;");

        cardsContainer.setAlignment(Pos.CENTER);
        cardsContainer.setPadding(new Insets(30, 20, 30, 20));

        goBackBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2B4C7E; -fx-font-size: 16px; -fx-cursor: hand;");
        errorLabel.setStyle("-fx-text-fill: #d9534f; -fx-font-size: 14px;");
    }

    public BorderPane buildRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #EBF2FA;");
        root.setTop(buildHeader());

        VBox centerBox = new VBox(20, pageTitle, errorLabel, cardsContainer);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setPadding(new Insets(30, 40, 20, 40));

        root.setCenter(centerBox);
        root.setBottom(buildFooter());
        return root;
    }

    private HBox buildHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(20, 40, 15, 40));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-border-color: #D0DFE8; -fx-border-width: 0 0 1 0;");

        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);

        InputStream logoStream = getClass().getResourceAsStream("/icons/Brand_logo.png");
        if (logoStream != null) {
            ImageView logo = new ImageView(new Image(logoStream, 40, 40, true, true));
            logoBox.getChildren().add(logo);
        }

        Label brand = new Label("DoseGuard");
        brand.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2B4C7E;");
        logoBox.getChildren().add(brand);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        InputStream profileStream = getClass().getResourceAsStream("/icons/Profile.png");
        ImageView profile = profileStream != null ? new ImageView(new Image(profileStream, 35, 35, true, true)) : new ImageView();

        header.getChildren().addAll(logoBox, spacer, profile);
        return header;
    }

    private HBox buildFooter() {
        HBox footer = new HBox(goBackBtn);
        footer.setPadding(new Insets(10, 40, 30, 40));
        footer.setAlignment(Pos.CENTER_LEFT);
        return footer;
    }

    // ── STEP 1: CARD SPECIALIZZAZIONI ──────────────────────────────────────────
    public void showSpecializations(List<SpecializationBean> specs, Consumer<SpecializationBean> onSelect) {
        pageTitle.setText("Select medical speciality");
        cardsContainer.getChildren().clear();

        for (SpecializationBean spec : specs) {
            VBox card = createBaseCard();

            // Mappa l'icona corrispondente alla specializzazione
            String iconName = switch (spec.getName().toLowerCase()) {
                case "cardiology" -> "Cardiology.png";
                case "neurology" -> "Neurology.png";
                default -> "Internal Med.png";
            };

            InputStream stream = getClass().getResourceAsStream("/icons/" + iconName);
            ImageView icon = stream != null ? new ImageView(new Image(stream, 70, 70, true, true)) : new ImageView();

            Label name = new Label(spec.getName());
            name.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2B4C7E;");

            card.getChildren().addAll(icon, name);
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

        int imgIndex = 1;
        for (DoctorBean doc : doctors) {
            VBox card = createBaseCard();

            // Cicla sulle immagini dei medici disponibili
            String doctorImgPath = "/images/Doctor " + (imgIndex > 3 ? 1 : imgIndex) + ".png";
            InputStream stream = getClass().getResourceAsStream(doctorImgPath);
            ImageView photo = stream != null ? new ImageView(new Image(stream, 90, 90, true, true)) : new ImageView();

            Label name = new Label("Dr. " + doc.getSurname());
            name.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2B4C7E;");

            Label details = new Label("Dott: " + doc.getFullName());
            styleDescriptionLabel(details);

            card.getChildren().addAll(photo, name, details);
            card.setOnMouseClicked(e -> onSelect.accept(doc));
            cardsContainer.getChildren().add(card);
            imgIndex++;
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
            ImageView icon = stream != null ? new ImageView(new Image(stream, 60, 60, true, true)) : new ImageView();

            Label dateLbl = new Label(slot.getDate().format(fmt));
            dateLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2B4C7E;");

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
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(220, 260);
        card.setStyle("-fx-background-color: #DCE5EE; -fx-background-radius: 20; -fx-padding: 20; -fx-cursor: hand;");
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #CBD9E8; -fx-background-radius: 20; -fx-padding: 20; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #DCE5EE; -fx-background-radius: 20; -fx-padding: 20; -fx-cursor: hand;"));
        return card;
    }

    private void styleDescriptionLabel(Label lbl) {
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F92A8; -fx-text-alignment: center;");
        lbl.setWrapText(true);
    }
}
