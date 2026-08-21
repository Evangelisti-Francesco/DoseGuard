package it.ispwproject.doseguard.view.cli;

import it.ispwproject.doseguard.enumerator.Role;

public class RegistrationView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("DoseGuard  –  Registrazione");
    }

    public String chiediCampo(String label) {
        return CLIRenderer.chiediCampo(label);
    }

    public String chiediPassword(String label) {
        return CLIRenderer.chiediCampo(label);   // In CLI il testo resta visibile
    }

    public Role chiediRuolo() {
        while (true) {
            CLIRenderer.sezione("Seleziona il tuo ruolo");
            CLIRenderer.voceMenu(1, "Paziente");
            CLIRenderer.voceMenu(2, "Medico");
            CLIRenderer.voceMenu(3, "Farmacista");
            String input = CLIRenderer.chiediSceltaStringa("Scelta [1-3]");

            if (input.equals("1")) return Role.PATIENT;
            if (input.equals("2")) return Role.DOCTOR;
            if (input.equals("3")) return Role.PHARMACIST;

            CLIRenderer.errore("Scelta non valida.");
        }
    }

    public void mostraSuccesso() {
        CLIRenderer.vuota();
        CLIRenderer.successo("Registrazione completata! Ora puoi effettuare il login.");
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }
}
