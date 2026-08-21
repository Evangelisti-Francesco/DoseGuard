package it.ispwproject.doseguard.view.cli;

import it.ispwproject.doseguard.bean.PatientBean;
import it.ispwproject.doseguard.bean.PatientProgressBean;
import it.ispwproject.doseguard.bean.TimeSlotBean;

import java.util.List;
import java.util.Scanner;

public class ManagePatientsView {

    private final Scanner scanner = new Scanner(System.in);

    public void mostraIntestazione() {
        CLIRenderer.intestazione("DoseGuard – Gestione Pazienti");
    }

    public void mostraPazienti(List<PatientBean> patients) {
        CLIRenderer.sezione("Elenco Pazienti Associati");
        if (patients.isEmpty()) {
            CLIRenderer.messaggio("Nessun paziente associato trovato.");
            return;
        }

        for (int i = 0; i < patients.size(); i++) {
            PatientBean p = patients.get(i);
            System.out.printf("  [%d] %s %s - CF: %s (Email: %s)%n",
                    (i + 1),
                    p.getName(),
                    p.getSurname(),
                    p.getFiscalCode() != null ? p.getFiscalCode() : "N/D",
                    p.getEmail());
        }
        System.out.println("  [0] Torna al menu principale");
    }

    public void mostraSchedaPaziente(PatientBean patient, List<TimeSlotBean> upcoming, PatientProgressBean progress) {
        CLIRenderer.intestazione("Scheda Paziente: " + patient.getName() + " " + patient.getSurname());

        // Note cliniche e progressi
        CLIRenderer.sezione("Note Cliniche");
        if (progress == null || progress.getNotes() == null || progress.getNotes().isBlank()) {
            CLIRenderer.messaggio("Nessuna nota registrata.");
        } else {
            CLIRenderer.messaggio("Note: " + progress.getNotes());
            if (progress.getUpdatedAt() != null) {
                CLIRenderer.messaggio("Ultimo aggiornamento: " + progress.getUpdatedAt());
            }
        }

        // Prossime visite
        CLIRenderer.sezione("Prossimi Appuntamenti");
        if (upcoming.isEmpty()) {
            CLIRenderer.messaggio("Nessuna visita futura programmata.");
        } else {
            for (TimeSlotBean slot : upcoming) {
                System.out.printf("  %s Data: %s | Ora: %s%n",
                        CLIRenderer.CLOCK,
                        slot.getDate(),
                        slot.getStartTime());
            }
        }
    }

    public void mostraMenuPaziente() {
        CLIRenderer.sezione("Opzioni Paziente");
        CLIRenderer.voceMenu(1, "Aggiorna/Aggiungi note cliniche");
        CLIRenderer.voceMenu(2, "Visualizza storico visite completate");
        CLIRenderer.voceMenuZero("Torna alla lista pazienti");
    }

    public void mostraStoricoVisite(List<TimeSlotBean> completed) {
        CLIRenderer.sezione("Storico Visite Effettuate (Completate)");
        if (completed.isEmpty()) {
            CLIRenderer.messaggio("Nessuna visita completata nello storico.");
        } else {
            for (TimeSlotBean slot : completed) {
                System.out.printf("  %s Data: %s | Ora: %s%n",
                        CLIRenderer.BULLET,
                        slot.getDate(),
                        slot.getStartTime());
            }
        }
    }

    public int chiediScelta(String prompt, int min, int max) {
        while (true) {
            System.out.print("\n" + prompt + " (" + min + "-" + max + "): ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= min && choice <= max) {
                    return choice;
                }
            } catch (NumberFormatException ignored) {
                // Gestito dal messaggio d'errore sotto
            }
            CLIRenderer.errore("Selezione non valida. Riprova.");
        }
    }

    public String chiediTesto(String etichetta) {
        System.out.print(etichetta + ": ");
        return scanner.nextLine().trim();
    }

    public void attesaInvio() {
        System.out.print("\nPremere INVIO per continuare...");
        scanner.nextLine();
    }

    public void mostraMessaggio(String message) {
        CLIRenderer.messaggio(message);
    }

    public void mostraSuccesso(String message) {
        CLIRenderer.successo(message);
    }

    public void mostraErrore(String message) {
        CLIRenderer.errore(message);
    }
}