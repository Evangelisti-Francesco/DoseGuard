package it.ispwproject.doseguard.controller.gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainGUI extends Application {

    public static final int WINDOW_WIDTH  = 900;
    public static final int WINDOW_HEIGHT = 580;

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setTitle("DoseGuard");
        stage.setWidth(WINDOW_WIDTH);
        stage.setHeight(WINDOW_HEIGHT);
        stage.setMinWidth(WINDOW_WIDTH);
        stage.setMinHeight(WINDOW_HEIGHT);
        stage.setResizable(true);

        showLogin();
    }

    public static void showLogin() {
        new LoginGUI(primaryStage).show();
    }

    public static void showRegistration() {
        new RegistrationGUI(primaryStage).show();
    }

    public static void showDashboardPatient() {
        new DashboardPatientGUI(primaryStage).show();
    }

    public static void showDashboardDoctor() {
        new DashboardDoctorGUI(primaryStage).show();
    }

    public static void showDashboardPharmacist() {
        new DashboardPharmacistGUI(primaryStage).show();
    }

    public static void launch(String[] args) {
        Application.launch(MainGUI.class, args);
    }
}
