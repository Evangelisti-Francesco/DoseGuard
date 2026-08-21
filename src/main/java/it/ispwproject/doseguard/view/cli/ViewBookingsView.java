package it.ispwproject.doseguard.view.cli;

import it.ispwproject.doseguard.bean.AppointmentResponseBean;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewBookingsView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("DoseGuard  –  I miei appuntamenti");
    }

    public void mostraTab(int nConfermate, int nCancellate, int nScadute) {
        CLIRenderer.vuota();
        CLIRenderer.voceMenu(1, "Confermati   (" + nConfermate + ")");
        CLIRenderer.voceMenu(2, "Cancellati   (" + nCancellate + ")");
        CLIRenderer.voceMenu(3, "Scaduti      (" + nScadute + ")");
        CLIRenderer.voceMenuZero("Indietro");
    }

    public void mostraConfermate(List<AppointmentResponseBean> bookings) {
        CLIRenderer.sezione("Confermati");
        if (bookings.isEmpty()) {
            CLIRenderer.messaggio("Non hai appuntamenti confermati.");
            CLIRenderer.separatore();
            return;
        }
        for (AppointmentResponseBean b : bookings) {
            CLIRenderer.vuota();
            System.out.println("  " + CLIRenderer.LINE_THIN);
            CLIRenderer.campo("Specializzazione", b.getSpecialization().getName());
            CLIRenderer.campo("Medico",           "Dott. " + b.getDoctor().getName() + " " + b.getDoctor().getSurname());
            CLIRenderer.campo("Data",             b.getSlot().getDate()
                    + "  " + b.getSlot().getStartTime());
            if (b.getDoctor().getEmail() != null) {
                CLIRenderer.campo("Email", b.getDoctor().getEmail());
            }
        }
        CLIRenderer.vuota();
        CLIRenderer.separatore();
    }

    public void mostraCancellate(List<AppointmentResponseBean> bookings) {
        CLIRenderer.sezione("Cancellati");
        if (bookings.isEmpty()) {
            CLIRenderer.messaggio("Non hai appuntamenti cancellati.");
            CLIRenderer.separatore();
            return;
        }
        for (AppointmentResponseBean b : bookings) {
            CLIRenderer.vuota();
            System.out.println("  " + CLIRenderer.LINE_THIN);
            CLIRenderer.campo("Specializzazione", b.getSpecialization().getName());
            CLIRenderer.campo("Medico",           "Dott. " + b.getDoctor().getName() + " " + b.getDoctor().getSurname());
            CLIRenderer.campo("Data",             b.getSlot().getDate()
                    + "  " + b.getSlot().getStartTime());
        }
        CLIRenderer.vuota();
        CLIRenderer.separatore();
    }

    public void mostraScadute(List<AppointmentResponseBean> past) {
        CLIRenderer.sezione("Scaduti");
        if (past.isEmpty()) {
            CLIRenderer.messaggio("Nessun appuntamento scaduto.");
            CLIRenderer.separatore();
            return;
        }

        // Raggruppa per specializzazione
        Map<String, List<AppointmentResponseBean>> bySpecialization = past.stream()
                .collect(Collectors.groupingBy(b -> b.getSpecialization().getName()));

        List<String> specializations = bySpecialization.keySet().stream().sorted().toList();

        for (String spec : specializations) {
            List<AppointmentResponseBean> group = bySpecialization.get(spec).stream()
                    .sorted((a, b) -> b.getSlot().getDate().compareTo(a.getSlot().getDate()))
                    .toList();

            CLIRenderer.vuota();
            System.out.printf("  %s  (%d %s)%n",
                    spec, group.size(), group.size() == 1 ? "visita" : "visite");
            System.out.println("  " + CLIRenderer.LINE_THIN);

            for (AppointmentResponseBean b : group) {
                System.out.printf("  %s  %s  %s – %s  Dott. %s%n",
                        CLIRenderer.BULLET,
                        b.getSlot().getDate(),
                        b.getSlot().getStartTime(),
                        b.getDoctor().getName() + " " + b.getDoctor().getSurname());
            }
        }
        CLIRenderer.vuota();
        CLIRenderer.separatore();
    }

    public int chiediScelta(String prompt, int min, int max) {
        return CLIRenderer.chiediScelta(prompt, min, max);
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }
}
