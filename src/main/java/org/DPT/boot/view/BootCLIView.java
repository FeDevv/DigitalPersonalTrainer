package org.DPT.boot.view;

import org.DPT.boot.model.UIMode;
import org.DPT.shared.ui.BaseCLIView;

/**
 * Gestisce ESCLUSIVAMENTE l'output a schermo per il boot.
 */
public class BootCLIView extends BaseCLIView {

    public void displayWelcome() {
        displayLine("=== DIGITAL PERSONAL TRAINER ===");
        displayLine("Inizializzazione sistema...\n");
    }

    public void displayMenu(UIMode[] modes) {
        displayLine("Seleziona la modalità di interfaccia:");
        for (UIMode mode : modes) {
            displayLine(mode.getId() + ") " + mode.getDescription());
        }
    }
}
