package it.ispwproject.doseguard.view.cli;

import it.ispwproject.doseguard.bean.PrescriptionBean;

import java.util.List;

public class ViewPrescriptionView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("DoseGuard  –  Le mie prescrizioni");
    }

    public void mostraPrescrizioni(List<PrescriptionBean> prescriptions) {
        if (prescriptions.isEmpty()) {
            CLIRenderer.messaggio("Nessuna prescrizione trovata.");
            return;
        }

        CLIRenderer.sezione("Elenco Prescrizioni");
        for (int i = 0; i < prescriptions.size(); i++) {
            PrescriptionBean p = prescriptions.get(i);
            String doctorInfo = !p.getDoctorFullName().isBlank()
                    ? "  (Medico: Dr. " + p.getDoctorFullName() + ")"
                    : "";

            System.out.printf("  [%d] Farmaco: %s  |  Dosaggio: %s  |  Frequenza: %s  |  Data: %s%s%n",
                    i + 1,
                    p.getDrug(),
                    p.getDosage(),
                    p.getFrequency(),
                    p.getIssueDate(),
                    doctorInfo);
        }
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