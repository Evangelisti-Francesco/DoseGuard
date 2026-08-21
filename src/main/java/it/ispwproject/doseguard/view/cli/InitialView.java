package it.ispwproject.doseguard.view.cli;

public class InitialView {

    public void mostraBenvenuto() {
        CLIRenderer.vuota();
        System.out.println(CLIRenderer.LINE_DECO);
        System.out.println(CLIRenderer.centra("D O S E G U A R D"));
        System.out.println(CLIRenderer.centra("La tua salute, sempre in orario!"));
        System.out.println(CLIRenderer.LINE_DECO);
    }

    public void mostraMenu() {
        CLIRenderer.vuota();
        CLIRenderer.voceMenu(1, "Accedi");
        CLIRenderer.voceMenu(2, "Registrati");
        CLIRenderer.voceMenuZero("Esci");
    }

    public String chiediScelta() {
        return CLIRenderer.chiediSceltaStringa("Scelta");
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }

    public void mostraArrivederci() {
        CLIRenderer.vuota();
        System.out.println(CLIRenderer.LINE_DECO);
        System.out.println(CLIRenderer.centra("Arrivederci!  –  DoseGuard"));
        System.out.println(CLIRenderer.LINE_DECO);
        CLIRenderer.vuota();
    }
}
