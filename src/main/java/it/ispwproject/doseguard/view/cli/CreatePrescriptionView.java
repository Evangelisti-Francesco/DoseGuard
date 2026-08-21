package it.ispwproject.doseguard.view.cli;

public class CreatePrescriptionView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("DoseGuard  –  Crea Nuova Prescrizione");
    }

    public int chiediPatientId() {
        return CLIRenderer.chiediScelta("Inserisci l'ID del paziente", 1, 99999);
    }

    // Metodo generico riutilizzabile per chiedere qualsiasi testo
    public String chiediCampo(String prompt) {
        return CLIRenderer.chiediCampo(prompt);
    }

    public void mostraSuccesso(String messaggio) {
        CLIRenderer.successo(messaggio);
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }

    public boolean chiediConferma(String prompt) {
        return CLIRenderer.chiediConferma(prompt);
    }
}
