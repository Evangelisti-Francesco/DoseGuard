package it.ispwproject.doseguard.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

public class DashboardPharmacistGUIView extends DashboardGUIView {

    public final Button dispenseBtn = new Button("Eroga Farmaco");

    public DashboardPharmacistGUIView() {
        dispenseBtn.getStyleClass().add("button");
        dispenseBtn.setPrefWidth(220);
        dispenseBtn.setPrefHeight(42);
    }

    public BorderPane buildRoot(String nomeutente, Runnable onLogout) {
        HBox navbar = buildNavbar("Farmacista", onLogout);

        VBox body = new VBox(20);
        body.getStyleClass().add("doseguard-background");
        body.setAlignment(Pos.CENTER);
        body.setPadding(new Insets(40));
        body.getChildren().add(dispenseBtn);

        BorderPane root = new BorderPane();
        root.getStyleClass().add("doseguard-background");
        root.setTop(navbar);
        root.setCenter(body);
        return root;
    }
}
