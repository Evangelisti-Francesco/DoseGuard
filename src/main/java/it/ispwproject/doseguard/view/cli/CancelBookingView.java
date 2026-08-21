package it.ispwproject.doseguard.view.cli;

import it.ispwproject.doseguard.bean.AppointmentResponseBean;

import java.util.List;

public class CancelBookingView {

    private static final String DOCTOR_PREFIX = "Dott. ";

    public void mostraIntestazione() {
        CLIRenderer.intestazione("DoseGuard  –  Annulla un appuntamento");
    }

    public void mostraPrenotazioniAnnullabili(List<AppointmentResponseBean> cancellable) {
        CLIRenderer.sezione("Appuntamenti attivi");

        // Larghezza colonne calcolata sui dati reali
        int specW = cancellable.stream()
                .mapToInt(b -> b.getSpecialization().getName().length())
                .max().orElse(15);
        int doctorW = cancellable.stream()
                .mapToInt(b -> ("DOCTOR_PREFIX. " + b.getDoctor().getName() + " " + b.getDoctor().getSurname()).length())
                .max().orElse(18);
        int numW = String.valueOf(cancellable.size()).length();

        String fmt = "  [%-" + numW + "d] %-" + specW + "s  %-" + doctorW + "s  %s  ore %s%n";

        for (int i = 0; i < cancellable.size(); i++) {
            AppointmentResponseBean b = cancellable.get(i);
            System.out.printf(fmt,
                    i + 1,
                    b.getSpecialization().getName(),
                    DOCTOR_PREFIX + b.getDoctor().getName() + " " + b.getDoctor().getSurname(),
                    b.getSlot().getDate(),
                    b.getSlot().getStartTime());
        }
        CLIRenderer.voceMenuZero("Indietro");
    }

    public void mostraRiepilogo(AppointmentResponseBean selected) {
        CLIRenderer.sezione("Conferma annullamento");
        CLIRenderer.campo("Specializzazione", selected.getSpecialization().getName());
        CLIRenderer.campo("Medico",           DOCTOR_PREFIX + selected.getDoctor().getName() + " " + selected.getDoctor().getSurname());
        CLIRenderer.campo("Data",             selected.getSlot().getDate() + "  ore " + selected.getSlot().getStartTime());
    }

    public void mostraSuccesso() {
        CLIRenderer.successo("Appuntamento annullato con successo.");
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }

    public int chiediScelta(String prompt, int min, int max) {
        return CLIRenderer.chiediScelta(prompt, min, max);
    }

    public boolean chiediConferma(String prompt) {
        return CLIRenderer.chiediConferma(prompt);
    }
}
