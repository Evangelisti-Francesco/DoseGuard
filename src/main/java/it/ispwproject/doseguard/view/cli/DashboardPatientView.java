package it.ispwproject.doseguard.view.cli;

public class DashboardPatientView {

    public void mostraBenvenuto(String nome) {
        CLIRenderer.intestazioneBenvenuto(nome, "Paziente");
    }

    public void mostraMenu() {
        CLIRenderer.sezione("Menu Paziente");
        CLIRenderer.voceMenu(1, "Prenota Visita Medica");
        CLIRenderer.voceMenu(2, "I Miei Appuntamenti");
        CLIRenderer.voceMenu(3, "Annulla Appuntamento");
        CLIRenderer.voceMenu(4, "I Miei Farmaci / Terapia");
        CLIRenderer.voceMenu(5, "Le Mie Ricette Mediche");
        CLIRenderer.voceMenu(6, "Modifica Profilo");
        CLIRenderer.voceMenuZero("Logout");
    }

    public String chiediScelta() {
        return CLIRenderer.chiediSceltaStringa("Scelta [0-6]");
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }
}
