package it.ispwproject.doseguard.controller.gui;

import it.ispwproject.doseguard.bean.*;
import it.ispwproject.doseguard.controller.applicativo.BookingController;
import it.ispwproject.doseguard.exception.BookingException;
import it.ispwproject.doseguard.exception.DAOException;
import it.ispwproject.doseguard.model.Patient;
import it.ispwproject.doseguard.pattern.singleton.SessionManager;
import it.ispwproject.doseguard.view.gui.BookAppointmentGUIView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookAppointmentGUI {

    private final Stage stage;
    private final BookingController bookingController = new BookingController();
    private final BookAppointmentGUIView view = new BookAppointmentGUIView();

    private SpecializationBean selectedSpecialization;
    private DoctorBean selectedDoctor;
    private TimeSlotBean selectedSlot;

    private int currentStep = 1;

    public BookAppointmentGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        bindBackButton();
        loadStep1Specializations();
        stage.setScene(GUIUtils.createScene(view.buildRoot()));
        stage.show();
    }

    private void loadStep1Specializations() {
        currentStep = 1;
        view.clearError();
        try {
            List<SpecializationBean> specs = bookingController.getAvailableSpecializations();
            view.showSpecializations(specs, this::onSpecializationSelected);
        } catch (DAOException e) {
            view.setError("Errore caricamento specializzazioni: " + e.getMessage());
        }
    }

    private void onSpecializationSelected(SpecializationBean spec) {
        this.selectedSpecialization = spec;
        currentStep = 2;
        view.clearError();
        try {
            List<DoctorBean> doctors = bookingController.getDoctorsBySpecialization(spec);
            view.showDoctors(doctors, this::onDoctorSelected);
        } catch (DAOException e) {
            view.setError("Errore caricamento medici: " + e.getMessage());
        }
    }

    private void onDoctorSelected(DoctorBean doctor) {
        this.selectedDoctor = doctor;
        currentStep = 3;
        view.clearError();
        try {
            List<TimeSlotBean> slots = bookingController.getDoctorAvailability(doctor)
                    .stream()
                    .filter(TimeSlotBean::isAvailable)
                    .toList();
            view.showTimeSlots(slots, this::onSlotSelected);
        } catch (DAOException e) {
            view.setError("Errore caricamento orari: " + e.getMessage());
        }
    }

    private void onSlotSelected(TimeSlotBean slot) {
        this.selectedSlot = slot;
        try {
            AppointmentRequestBean request = buildRequest();
            bookingController.prepareBookingSummary(request);
            showCountdownDialog(request);
        } catch (DAOException | BookingException ex) {
            view.setError("Errore: " + ex.getMessage());
        }
    }

    private void bindBackButton() {
        view.goBackBtn.setOnAction(e -> {
            if (currentStep == 3) {
                onSpecializationSelected(selectedSpecialization);
            } else if (currentStep == 2) {
                loadStep1Specializations();
            } else {
                MainGUI.showDashboardPatient();
            }
        });
    }

    private void showCountdownDialog(AppointmentRequestBean request) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int[] secondsLeft = {180};

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Conferma prenotazione visita");
        confirm.setHeaderText("⏱ Slot riservato per 3 minuti");

        Label contentLabel = new Label(buildSummaryText(fmt, 3, 0));
        confirm.getDialogPane().setContent(contentLabel);

        Timeline countdown = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            secondsLeft[0]--;
            int min = secondsLeft[0] / 60;
            int sec = secondsLeft[0] % 60;
            contentLabel.setText(buildSummaryText(fmt, min, sec));
            if (secondsLeft[0] <= 0) {
                confirm.close();
                try {
                    bookingController.releaseSlot(selectedSlot.getId());
                } catch (DAOException ex) {
                    /* ignora */
                }
                Platform.runLater(() -> view.setError("Tempo scaduto. Lo slot è stato rilasciato."));
            }
        }));
        countdown.setCycleCount(180);
        countdown.play();

        confirm.showAndWait().ifPresent(r -> {
            countdown.stop();
            if (r == ButtonType.OK) {
                confirmBooking();
            } else {
                try {
                    bookingController.releaseSlot(selectedSlot.getId());
                } catch (DAOException ex) {
                    view.setError("Errore: " + ex.getMessage());
                }
            }
        });
    }

    private String buildSummaryText(DateTimeFormatter fmt, int min, int sec) {
        return "Specializzazione: " + selectedSpecialization.getName() + "\n" +
                "Medico:           Dr. " + selectedDoctor.getFullName() + "\n" +
                "Giorno:           " + selectedSlot.getDate().format(fmt) + "\n" +
                "Orario:           " + selectedSlot.getStartTime() + "\n\n" +
                String.format("Tempo rimasto: %d:%02d", min, sec);
    }

    private void confirmBooking() {
        try {
            AppointmentResponseBean response = bookingController.createBooking(buildRequest());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Prenotazione confermata");
            alert.setHeaderText(null);
            alert.setContentText("✓ Visita medica prenotata con successo!");
            alert.showAndWait();

            MainGUI.showDashboardPatient();
        } catch (BookingException | DAOException e) {
            try {
                bookingController.releaseSlot(selectedSlot.getId());
            } catch (DAOException ex) {
                /* ignora */
            }
            view.setError("Errore: " + e.getMessage());
        }
    }

    private AppointmentRequestBean buildRequest() {
        Patient p = (Patient) SessionManager.getInstance().getLoggedUser();
        PatientBean pb = new PatientBean(p.getId(), p.getName(), p.getSurname(), p.getEmail(),p.getFiscalCode());
        return new AppointmentRequestBean(pb, selectedDoctor, selectedSpecialization, selectedSlot,"");
    }
}