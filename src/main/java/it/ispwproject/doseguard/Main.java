package it.ispwproject.doseguard;

import it.ispwproject.doseguard.controller.cli.MainCLI;
import it.ispwproject.doseguard.controller.cli.ModeSelectorCLI; // Rimuovi se non lo hai ancora creato
// import it.ispwproject.doseguard.controller.gui.MainGUI;     // Decommenta quando avrai la GUI

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
            System.out.println("\n  ── Seleziona interfaccia");
            System.out.println("  [1] CLI  — interfaccia testuale");
            System.out.println("  [2] GUI  — interfaccia grafica");
            System.out.print("\n  Scelta: ");
            scelta = scanner.nextLine().trim();

            if (!scelta.equals("1") && !scelta.equals("2")) {
                CLIRenderer.errore("Scelta non valida.");
            }
        }

        if (scelta.equals("2")) {
            // MainGUI.launch(args);
            CLIRenderer.messaggio("Interfaccia GUI non ancora disponibile.");
        } else {
            MainCLI.start();
        }
    }
}
