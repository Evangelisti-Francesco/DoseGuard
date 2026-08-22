package it.ispwproject.doseguard.view.gui;

import it.ispwproject.doseguard.bean.PrescriptionBean;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

public class DashboardPatientGUIView extends DashboardGUIView {

    private static final String PRESCRIPTION_COLOR = "#3498DB"; // Azzurro DoseGuard

    public final ScrollPane calendarScroll = new ScrollPane();

    // ────────────────────────────────────────────────────────────────────────
    // Sezione Calendario Paziente
    // ────────────────────────────────────────────────────────────────────────

    public VBox buildCalendarSection(Runnable onPrev, Runnable onNext, Runnable onToday) {
        return super.buildCalendarSection(onPrev, onNext, onToday, calendarScroll);
    }

    public void refreshCalendar(List<PrescriptionBean> prescriptions, int weekOffset) {
        double w = calendarScroll.getWidth() > 10 ? calendarScroll.getWidth() : 560;
        calendarScroll.setContent(buildWeekCalendar(prescriptions, weekOffset, w));
    }

    public void bindCalendarWidth(List<PrescriptionBean> prescriptions, int[] weekOffsetRef) {
        calendarScroll.widthProperty().addListener((obs, oldW, newW) -> {
            if (newW.doubleValue() > 10)
                calendarScroll.setContent(
                        buildWeekCalendar(prescriptions, weekOffsetRef[0], newW.doubleValue()));
        });
    }

    private Pane buildWeekCalendar(List<PrescriptionBean> prescriptions, int weekOffset, double availWidth) {
        LocalDate today  = LocalDate.now(ZoneId.systemDefault());
        LocalDate monday = today.with(DayOfWeek.MONDAY).plusWeeks(weekOffset);

        int totalHours = HOUR_END - HOUR_START;
        int gridHeight = totalHours * HOUR_HEIGHT;
        int colW = (int) Math.max(48, (availWidth - LABEL_WIDTH - 2) / DAYS);

        Pane pane = buildCalendarPane(monday, colW);
        addMonthRow(pane, monday, colW);
        addDayHeaders(pane, monday, today, colW);
        addHourRows(pane, totalHours, colW, gridHeight);
        addPrescriptionBlocks(pane, prescriptions, monday, totalHours, colW);
        return pane;
    }

    private void addPrescriptionBlocks(Pane pane, List<PrescriptionBean> prescriptions,
                                       LocalDate firstDay, int totalHours, int colW) {
        for (PrescriptionBean p : prescriptions) {
            // Usa la data di emissione o di assunzione
            LocalDate pDate = p.getIssueDate();
            if (pDate == null) continue;

            // Impostiamo un orario di default (es. 09:00 - 10:00) per la visualizzazione nel calendario
            LocalTime pStart = LocalTime.of(9, 0);
            LocalTime pEnd   = LocalTime.of(10, 0);

            int dayOffset = -1;
            for (int d = 0; d < DAYS; d++) {
                if (firstDay.plusDays(d).equals(pDate)) {
                    dayOffset = d;
                    break;
                }
            }
            if (dayOffset < 0) continue;

            double sf = (pStart.getHour() + pStart.getMinute() / 60.0) - HOUR_START;
            double ef = (pEnd.getHour()   + pEnd.getMinute()   / 60.0) - HOUR_START;
            if (sf < 0 || ef > totalHours) continue;

            VBox block = new VBox(1);
            block.setLayoutX(LABEL_WIDTH + dayOffset * colW + 2);
            block.setLayoutY(HEADER_H + sf * HOUR_HEIGHT);
            block.setPrefWidth(colW - 4);
            block.setPrefHeight(Math.max((ef - sf) * HOUR_HEIGHT - 2, 20));
            block.setPadding(new Insets(2, 3, 2, 3));
            block.setStyle("-fx-background-color: " + PRESCRIPTION_COLOR + "; -fx-background-radius: 4;");

            Label s = new Label(p.getDrug() + " (" + p.getDosage() + ")");
            s.getStyleClass().add("calendar-block-title");
            s.setWrapText(true);

            Label t = new Label(p.getFrequency() != null ? p.getFrequency() : "Prescrizione");
            t.getStyleClass().add("calendar-block-time");
            block.getChildren().addAll(s, t);

            // Tooltip con i campi effettivi del bean
            Tooltip tooltip = new Tooltip(
                    "Farmaco: " + p.getDrug() + "\n" +
                            "Dosaggio: " + p.getDosage() + "\n" +
                            "Frequenza: " + p.getFrequency() + "\n" +
                            "Medico: " + p.getDoctorFullName());
            Tooltip.install(block, tooltip);

            // Click → Dialog Informativo
            block.setOnMouseClicked(e -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Dettagli Prescrizione");
                alert.setHeaderText(p.getDrug());
                alert.setContentText(
                        "Dosaggio: " + p.getDosage() + "\n" +
                                "Frequenza: " + p.getFrequency() + "\n" +
                                "Prescritto da: " + p.getDoctorFullName() + "\n" +
                                "Data Emissione: " + pDate + "\n" +
                                "Codice Fiscale Paziente: " + p.getPatientFiscalCode());
                alert.showAndWait();
            });
            block.setStyle(block.getStyle() + " -fx-cursor: hand;");

            pane.getChildren().add(block);
        }
    }


    public VBox buildRightSection(VBox actionButtons, VBox accordion) {
        VBox section = new VBox(14);
        section.setAlignment(Pos.TOP_CENTER);
        section.setPrefWidth(320);
        section.setMinWidth(320);
        section.setPadding(new Insets(0));

        Region spacer = new Region();
        spacer.setPrefHeight(40);
        spacer.setMinHeight(40);
        spacer.setMaxHeight(40);

        VBox.setVgrow(actionButtons, Priority.NEVER);
        VBox.setVgrow(accordion,     Priority.NEVER);

        section.getChildren().addAll(spacer, actionButtons, accordion);
        return section;
    }

    public VBox buildActionButtons(EventHandler<ActionEvent> onBook,
                                   EventHandler<ActionEvent> onViewPrescriptions) {
        VBox buttons = new VBox(14);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(
                buildActionTile("New appointment.png", "Prenota Visita",      onBook),
                buildActionTile("File.png",     "Le Mie Prescrizioni", onViewPrescriptions)
        );
        return buttons;
    }
}