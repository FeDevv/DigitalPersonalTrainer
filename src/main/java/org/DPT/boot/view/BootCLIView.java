package org.DPT.boot.view;

import org.DPT.boot.model.UIMode;

/**
 * Gestisce ESCLUSIVAMENTE l'output a schermo.
 * Non contiene logica, non legge input. È un puro display.
 */
public class BootCLIView {

    public void displayWelcome() {
        System.out.println("=== DIGITAL PERSONAL TRAINER ===");
        System.out.println("Inizializzazione sistema...\n");
    }

    public void displayMenu(UIMode[] modes) {
        System.out.println("Seleziona la modalità di interfaccia:");
        for (UIMode mode : modes) {
            System.out.println(mode.getId() + ") " + mode.getDescription());
        }
    }

    public void displayInputPrompt() {
        System.out.print("\nInserisci l'ID della tua scelta: ");
    }

    public void displayError(String errorMessage) {
        System.out.println("Errore: " + errorMessage + "\n");
    }
}
