package it.ispwproject.doseguard.view.cli;

import java.util.Scanner;

public class DashboardPharmacistView {

    private final Scanner scanner = new Scanner(System.in);

    public void mostraBenvenuto(String nome) {
        CLIRenderer.intestazione("DoseGuard – Dashboard Farmacista");
        CLIRenderer.messaggio("Benvenuto/a, " + nome + "!");
    }

    public void mostraMenu() {
        CLIRenderer.sezione("Menu Principale");
        CLIRenderer.voceMenu(1, "Visualizza ricette / prescrizioni paziente");
        CLIRenderer.voceMenu(2, "Eroga farmaco / Convalida prescrizione");
        CLIRenderer.voceMenuZero("Logout");
    }

    public String chiediScelta() {
        System.out.print("\nScelta: ");
        return scanner.nextLine().trim();
    }

    public void mostraMessaggio(String message) {
        CLIRenderer.messaggio(message);
    }
}