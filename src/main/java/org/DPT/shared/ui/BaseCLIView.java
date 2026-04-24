package org.DPT.shared.ui;

/**
 * Classe base per tutte le View CLI del sistema.
 * Fornisce metodi standard per la formattazione dell'output.
 */
public class BaseCLIView {

    public void displayHeader(String title) {
        System.out.println("\n========================================");
        System.out.printf("     DIGITAL PERSONAL TRAINER - %s   %n", title.toUpperCase());
        System.out.println("========================================");
    }

    public void displaySectionTitle(String title) {
        System.out.println("\n--- " + title.toUpperCase() + " ---");
    }

    public void displayLabel(String label) {
        System.out.print(label + ": ");
    }

    public void displayInputPrompt(String prompt) {
        System.out.print(prompt);
    }

    public void displayError(String message) {
        System.out.println("[ERRORE] " + message);
    }

    public void displaySuccess(String message) {
        System.out.println("\n[OK] " + message);
    }

    public void displayLine(String text) {
        System.out.println(text);
    }

    public void displayEmptyLine() {
        System.out.println();
    }
}
