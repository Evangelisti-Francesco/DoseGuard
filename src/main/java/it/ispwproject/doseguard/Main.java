package it.ispwproject.doseguard;

//import it.ispwproject.doseguard.controller.cli.MainCLI;
//import it.ispwproject.doseguard.controller.cli.ModeSelectorCLI;
//import it.ispwproject.doseguard.controller.gui.MainGUI;
import it.ispwproject.doseguard.util.Printer;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // ── Step 1 — Selezione modalità persistenza (DAO Demo / DB) ──
        /*ModeSelectorCLI modeSelector = new ModeSelectorCLI();
        boolean proceed = modeSelector.start();
        if (!proceed) return;

        // ── Step 2 — Selezione interfaccia (CLI vs GUI) ─────────────
        Scanner scanner = new Scanner(System.in);
        String scelta = "";

        while (!scelta.equals("1") && !scelta.equals("2")) {
            Printer.println("\n  ── Dose Guard: Seleziona Interfaccia");
            Printer.println("  [1] CLI  — Interfaccia da terminale");
            Printer.println("  [2] GUI  — Interfaccia grafica (JavaFX)");
            Printer.print("\n  Scelta: ");
            scelta = scanner.nextLine().trim();
            if (!scelta.equals("1") && !scelta.equals("2")) {
                Printer.printError("Scelta non valida. Riprova.");
            }
        }

        if ("2".equals(scelta)) {
            MainGUI.launchApp(args);
        } else {
            MainCLI.start();
        }

         */
    }
}
