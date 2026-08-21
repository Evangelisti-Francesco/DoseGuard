package it.ispwproject.doseguard.view.cli;

public class DashboardDoctorView {

    public void mostraBenvenuto(String nome) {
        CLIRenderer.intestazioneBenvenuto(nome, "Medico");
    }

    public void mostraMenu() {
        CLIRenderer.vuota();
        CLIRenderer.voceMenu(1, "Crea prescrizione");
        CLIRenderer.voceMenu(2, "Visualizza prescrizioni");
        CLIRenderer.voceMenu(3, "Aggiungi disponibilità appuntamenti");
        CLIRenderer.voceMenu(4, "I miei slot appuntamenti");
        CLIRenderer.voceMenu(5, "Gestisci pazienti");
        CLIRenderer.voceMenu(6, "Profilo");
        CLIRenderer.voceMenuZero("Logout");
    }

    public String chiediScelta() {
        return CLIRenderer.chiediSceltaStringa("Scelta");
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }
}
