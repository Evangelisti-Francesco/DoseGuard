package it.ispwproject.doseguard.view.gui;

import it.ispwproject.doseguard.bean.PrescriptionBean;
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

public class ViewPrescriptionsGUIView {

    public final Label pageTitle = new Label("My Prescriptions");
    public final VBox prescriptionsContainer = new VBox(15);
    public final Button goBackBtn = new Button("← Go Back");
    public final Label errorLabel = new Label("");

    public ViewPrescriptionsGUIView() {
        pageTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2B4C7E;");
        prescriptionsContainer.setAlignment(Pos.TOP_CENTER);
        prescriptionsContainer.setPadding(new Insets(20));

        goBackBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2B4C7E; -fx-font-size: 16px; -fx-cursor: hand;");
        errorLabel.setStyle("-fx-text-fill: #d9534f; -fx-font-size: 14px;");
    }

    public BorderPane buildRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #EBF2FA;");
        root.setTop(buildHeader());

        VBox centerBox = new VBox(20, pageTitle, errorLabel, prescriptionsContainer);
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

    public void showPrescriptions(List<PrescriptionBean> prescriptions, Consumer<PrescriptionBean> onSelect) {
        prescriptionsContainer.getChildren().clear();

        if (prescriptions.isEmpty()) {
            Label emptyLbl = new Label("No active prescriptions found.");
            emptyLbl.setStyle("-fx-font-size: 16px; -fx-text-fill: #7F92A8;");
            prescriptionsContainer.getChildren().add(emptyLbl);
            return;
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (PrescriptionBean prescription : prescriptions) {
            HBox card = createPrescriptionCard(prescription, fmt);
            if (onSelect != null) {
                card.setOnMouseClicked(e -> onSelect.accept(prescription));
            }
            prescriptionsContainer.getChildren().add(card);
        }
    }

    private HBox createPrescriptionCard(PrescriptionBean p, DateTimeFormatter fmt) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(700);
        card.setStyle("-fx-background-color: #DCE5EE; -fx-background-radius: 15; -fx-padding: 15 25 15 25; -fx-cursor: hand;");

        InputStream stream = getClass().getResourceAsStream("/icons/Prescription.png");
        ImageView icon = stream != null ? new ImageView(new Image(stream, 45, 45, true, true)) : new ImageView();

        VBox detailsBox = new VBox(5);
        Label medName = new Label(p.getDrug());
        medName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2B4C7E;");

        Label doctorLbl = new Label("Dr. " + p.getDoctorFullName() + " | Dosaggio: " + p.getDosage());
        doctorLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #5A6E85;");

        detailsBox.getChildren().addAll(medName, doctorLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(icon, detailsBox, spacer);

        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #CBD9E8; -fx-background-radius: 15; -fx-padding: 15 25 15 25; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #DCE5EE; -fx-background-radius: 15; -fx-padding: 15 25 15 25; -fx-cursor: hand;"));

        return card;
    }

    public void setError(String msg) {
        errorLabel.setText(msg);
    }

    public void clearError() {
        errorLabel.setText("");
    }
}
