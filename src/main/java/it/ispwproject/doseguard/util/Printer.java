package it.ispwproject.doseguard.util;

@SuppressWarnings("java:S106")
public final class Printer {

    // Codici ANSI per i colori in console
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED   = "\u001B[31m";
    private static final String ANSI_BLUE  = "\u001B[34m"; // Blu per DoseGuard
    private static final String ANSI_GREEN = "\u001B[32m";

    private Printer() {}

    public static void print(String message) {
        System.out.print(message);
    }

    public static void println(String message) {
        System.out.println(message);
    }

    public static void printTitle(String message) {
        System.out.print(ANSI_BLUE + message + ANSI_RESET);
    }

    public static void printlnTitle(String message) {
        System.out.println(ANSI_BLUE + message + ANSI_RESET);
    }

    public static void printError(String message) {
        System.out.println(ANSI_RED + "❌ " + message + ANSI_RESET);
    }

    public static void printSuccess(String message) {
        System.out.println(ANSI_GREEN + "✅ " + message + ANSI_RESET);
    }
}