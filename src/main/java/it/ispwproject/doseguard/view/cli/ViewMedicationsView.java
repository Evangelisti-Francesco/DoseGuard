package it.ispwproject.doseguard.view.cli;

import it.ispwproject.doseguard.bean.MedicationBean;

import java.util.List;

public class ViewMedicationsView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("DoseGuard  –  I miei farmaci");
    }

    public void mostraFarmaci(List<MedicationBean> medications) {
        if (medications.isEmpty()) {
            CLIRenderer.messaggio("Nessun farmaco in elenco.");
            return;
        }

        // ── Da assumere
        CLIRenderer.sezione("Da assumere");
        boolean hasPending = false;
        for (int i = 0; i < medications.size(); i++) {
            MedicationBean m = medications.get(i);
            if (!m.isTaken()) {
                System.out.printf("  [%d] %s  %s - Dosaggio: %s%n",
                        i + 1, CLIRenderer.PENDING, m.getName(), m.getDosage());
                hasPending = true;
            }
        }
        if (!hasPending) CLIRenderer.messaggio("Nessun farmaco in attesa di assunzione.");

        // ── Assunti
        CLIRenderer.sezione("Assunti");
        boolean hasTaken = false;
        for (MedicationBean m : medications) {
            if (m.isTaken()) {
                System.out.printf("  %s  %s - Dosaggio: %s%n",
                        CLIRenderer.DONE, m.getName(), m.getDosage());
                hasTaken = true;
            }
        }
        if (!hasTaken) CLIRenderer.messaggio("Nessun farmaco già assunto.");
    }

    public void mostraPendingPerSelezione(List<MedicationBean> pending) {
        CLIRenderer.sezione("Segna come assunto");
        for (int i = 0; i < pending.size(); i++) {
            System.out.printf("  [%d] %s  %s - Dosaggio: %s%n",
                    i + 1, CLIRenderer.PENDING, pending.get(i).getName(), pending.get(i).getDosage());
        }
        CLIRenderer.voceMenuZero("Annulla");
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

    public int chiediScelta(String prompt, int min, int max) {
        return CLIRenderer.chiediScelta(prompt, min, max);
    }
}