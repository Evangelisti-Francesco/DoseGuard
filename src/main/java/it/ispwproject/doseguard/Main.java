package it.ispwproject.doseguard;

import it.ispwproject.doseguard.controller.cli.MainCLI;
import it.ispwproject.doseguard.controller.cli.ModeSelectorCLI;
import it.ispwproject.doseguard.controller.gui.MainGUI;
import it.ispwproject.doseguard.util.Printer;
import it.ispwproject.doseguard.view.cli.CLIRenderer;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // ── Step 1 — Selezione modalità persistenza ──────────────────
        ModeSelectorCLI modeSelector = new ModeSelectorCLI();
        boolean proceed = modeSelector.start();
        if (!proceed) return;

        // ── Step 2 — Selezione interfaccia ───────────────────────────
        Scanner scanner = new Scanner(System.in);
        String scelta = "";

        while (!scelta.equals("1") && !scelta.equals("2")) {
            Printer.println("\n  ── Seleziona interfaccia");
            Printer.println("  [1] CLI  — interfaccia testuale");
            Printer.println("  [2] GUI  — interfaccia grafica");
            Printer.print("\n  Scelta: ");

            scelta = scanner.nextLine().trim();

            if (!scelta.equals("1") && !scelta.equals("2")) {
                CLIRenderer.errore("Scelta non valida.");
            }
        }

        if (scelta.equals("2")) {
            MainGUI.launch(args);
        } else {
            MainCLI.start();
        }
    }
}
