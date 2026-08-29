package it.ispwproject.doseguard.view.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class DashboardGUIView {

    protected static final String BRAND_BLUE = "#2551D8";

    public BorderPane buildDashboardRoot(String roleTitle, EventHandler<ActionEvent> profileHandler, VBox... cards) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #DDE5ED;");

        // Contenitore bianco centrale principale
        VBox mainContainer = new VBox(25);
        mainContainer.setMaxWidth(1100);
        mainContainer.setMaxHeight(680);
        mainContainer.setPadding(new Insets(30, 45, 45, 45));
        mainContainer.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 5);");

        // Top Bar (Logo + Immagine Profilo in alto a destra che fa il Logout)
        BorderPane topBar = new BorderPane();
        Label logoLabel = new Label("DoseGuard");
        logoLabel.setStyle("-fx-text-fill: " + BRAND_BLUE + "; -fx-font-size: 26px; -fx-font-weight: bold;");

        // Caricamento dell'immagine Profile.png (assicurati che sia nella cartella delle risorse o nel package corretto)
        javafx.scene.image.ImageView profileImage = new javafx.scene.image.ImageView(
                getClass().getResource("/icons/Profile.png").toExternalForm()
        );
        profileImage.setFitWidth(24);
        profileImage.setFitHeight(24);
        profileImage.setPreserveRatio(true);

        // Pulsante circolare che racchiude l'immagine del profilo
        Button profileBtn = new Button();
        profileBtn.setGraphic(profileImage);
        profileBtn.setOnAction(profileHandler);

        profileBtn.setShape(new javafx.scene.shape.Circle(20));
        profileBtn.setMinSize(42, 42);
        profileBtn.setMaxSize(42, 42);
        profileBtn.setStyle(
                "-fx-background-color: #E5E7EB; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 21; " +
                        "-fx-border-color: #D1D5DB; " +
                        "-fx-border-radius: 21;"
        );

        topBar.setLeft(logoLabel);
        topBar.setRight(profileBtn);

        // Titolo della pagina
        Label titleLabel = new Label(roleTitle);
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + BRAND_BLUE + ";");

        // Griglia dinamica delle card
        GridPane gridPane = new GridPane();
        gridPane.setHgap(25);
        gridPane.setVgap(20);
        gridPane.setAlignment(Pos.CENTER);

        // Inserisce le card passate come parametro in una griglia (massimo 3 colonne)
        int col = 0;
        int row = 0;
        for (VBox card : cards) {
            gridPane.add(card, col, row);
            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }

        mainContainer.getChildren().addAll(topBar, titleLabel, gridPane);

        StackPane centerContainer = new StackPane(mainContainer);
        centerContainer.setPadding(new Insets(20));
        root.setCenter(centerContainer);

        return root;
    }

    /**
     * Metodo di supporto riutilizzabile per creare una singola card con stile coerente.
     */
    public VBox createFeatureCard(Button cardButton, String titleText, String descText) {
        VBox cardContent = new VBox(10);
        cardContent.setAlignment(Pos.CENTER);
        cardContent.setPadding(new Insets(20, 15, 20, 15));

        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + BRAND_BLUE + ";");

        Label description = new Label(descText);
        description.setWrapText(true);
        description.setAlignment(Pos.CENTER);
        description.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280;");

        cardContent.getChildren().addAll(title, description);

        cardButton.setGraphic(cardContent);
        cardButton.setPrefSize(310, 160);
        cardButton.setStyle(
                "-fx-background-color: #E5E7EB; -fx-background-radius: 14; -fx-cursor: hand; -fx-border-color: transparent;"
        );

        cardButton.setOnMouseEntered(e -> cardButton.setStyle("-fx-background-color: #D1D5DB; -fx-background-radius: 14; -fx-cursor: hand;"));
        cardButton.setOnMouseExited(e -> cardButton.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 14; -fx-cursor: hand;"));

        return new VBox(cardButton);
    }
}
