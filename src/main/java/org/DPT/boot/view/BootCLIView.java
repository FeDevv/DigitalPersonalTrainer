package org.DPT.boot.view;

import org.DPT.boot.model.UIMode;

/**
 * Gestisce ESCLUSIVAMENTE l'output a schermo per il boot.
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
        // Un solo newline prima del prompt per staccarlo dal menu o dall'errore
        System.out.print("\n>> ");
    }

    public void displayError(String errorMessage) {
        // Nessun newline finale, ci pensa il println.
        System.out.println("[ERRORE] " + errorMessage);
    }
}
