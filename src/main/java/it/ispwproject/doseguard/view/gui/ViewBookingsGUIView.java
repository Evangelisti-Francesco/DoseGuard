package it.ispwproject.doseguard.view.gui;

import it.ispwproject.doseguard.bean.AppointmentResponseBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ViewBookingsGUIView {

    public final Label pageTitle = new Label("I miei appuntamenti");
    public final Button goBackBtn = new Button("← Go Back");
    public final Label errorLabel = new Label("");

    // Pulsanti per i tab
    public final ToggleButton confirmedTabBtn = new ToggleButton("Confermati (0)");
    public final ToggleButton cancelledTabBtn = new ToggleButton("Cancellati (0)");
    public final ToggleButton pastTabBtn = new ToggleButton("Scaduti (0)");

    // Contenitore della lista degli appuntamenti
    public final VBox bookingsListContainer = new VBox(12);

    public ViewBookingsGUIView() {
        pageTitle.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2551D8;");
        errorLabel.setStyle("-fx-text-fill: #d9534f; -fx-font-size: 14px;");

        goBackBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #2551D8; -fx-font-size: 16px; -fx-cursor: hand;");

        // Stile dei pulsanti Tab stile pillola
        String tabStyle =
                "-fx-background-color: #E5E7EB; -fx-text-fill: #374151; -fx-font-size: 14px; " +
                        "-fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 8 18; -fx-cursor: hand;";

        confirmedTabBtn.setStyle(tabStyle);
        cancelledTabBtn.setStyle(tabStyle);
        pastTabBtn.setStyle(tabStyle);
    }

    public BorderPane buildRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #DDE5ED;");

        // Contenitore bianco centrale principale
        BorderPane mainContainer = new BorderPane();
        mainContainer.setPrefWidth(1100);
        mainContainer.setPrefHeight(740);
        mainContainer.setPadding(new Insets(30, 45, 30, 45));
        mainContainer.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 5);");

        // TOP SECTION (Header + Titolo + Tab di navigazione)
        VBox topSection = new VBox(20);
        BorderPane header = buildHeaderInsideCard();
        VBox titleBox = new VBox(5, pageTitle, errorLabel);

        // HBox per i tab di selezione
        HBox tabsBox = new HBox(15, confirmedTabBtn, cancelledTabBtn, pastTabBtn);
        tabsBox.setAlignment(Pos.CENTER_LEFT);

        topSection.getChildren().addAll(header, titleBox, tabsBox);
        mainContainer.setTop(topSection);

        // CENTER SECTION (Lista degli appuntamenti scorrevole)
        bookingsListContainer.setAlignment(Pos.TOP_LEFT);
        bookingsListContainer.setPadding(new Insets(10, 5, 10, 5));

        ScrollPane scrollPane = new ScrollPane(bookingsListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white; -fx-background-color: white; -fx-border-color: transparent;");

        mainContainer.setCenter(scrollPane);

        // BOTTOM SECTION (Tasto Go Back)
        HBox footer = new HBox(goBackBtn);
        footer.setPadding(new Insets(10, 0, 0, 0));
        footer.setAlignment(Pos.CENTER_LEFT);
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

    // Metodo di supporto per popolare la lista degli appuntamenti graficamente a card
    public void renderAppointments(List<AppointmentResponseBean> bookings, String emptyMessage) {
        bookingsListContainer.getChildren().clear();

        if (bookings.isEmpty()) {
            Label emptyLbl = new Label(emptyMessage);
            emptyLbl.setStyle("-fx-font-size: 15px; -fx-text-fill: #6B7280; -fx-padding: 20 0;");
            bookingsListContainer.getChildren().add(emptyLbl);
            return;
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (AppointmentResponseBean b : bookings) {
            VBox card = new VBox(6);
            card.setPadding(new Insets(15, 20, 15, 20));
            card.setStyle(
                    "-fx-background-color: #F9FAFB; -fx-background-radius: 12; " +
                            "-fx-border-color: #E5E7EB; -fx-border-radius: 12;"
            );

            Label specLbl = new Label(b.getSpecialization().getName());
            specLbl.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #2551D8;");

            String docName = "Dr. " + b.getDoctor().getName() + " " + b.getDoctor().getSurname();
            Label docLbl = new Label("Medico: " + docName);
            docLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #374151;");

            String dateStr = b.getSlot().getDate().format(fmt) + " alle " + b.getSlot().getStartTime();
            Label dateLbl = new Label("Data: " + dateStr);
            dateLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #4B5563;");

            card.getChildren().addAll(specLbl, docLbl, dateLbl);

            if (b.getDoctor().getEmail() != null && !b.getDoctor().getEmail().isEmpty()) {
                Label emailLbl = new Label("Email: " + b.getDoctor().getEmail());
                emailLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280;");
                card.getChildren().add(emailLbl);
            }

            bookingsListContainer.getChildren().add(card);
        }
    }

    public void setError(String msg) {
        errorLabel.setText(msg);
    }

}
