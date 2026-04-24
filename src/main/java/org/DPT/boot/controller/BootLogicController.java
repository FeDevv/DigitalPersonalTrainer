package org.DPT.boot.controller;

import org.DPT.boot.model.Configuration;
import org.DPT.boot.model.UIMode;

import java.util.Locale;
import java.util.Scanner;

/**
 * Il Controller Logico Principale del Boot.
 */
public class BootLogicController {

    private final BootCLIController cliController;

    public BootLogicController(Scanner sharedScanner) {
        this.cliController = new BootCLIController(sharedScanner);
    }

    public Configuration execute(String[] args) {
        // Gestione parametri da riga di comando (priorità massima)
        if (args != null && args.length > 0) {
            for (String arg : args) {
                if (arg.equalsIgnoreCase("--gui")) return Configuration.defaultGUI();
                if (arg.equalsIgnoreCase("--cli")) return Configuration.defaultCLI();
            }
        }

        cliController.showWelcome();
        cliController.showMenu();
        
        UIMode selectedMode = null;
        while (selectedMode == null) {
            int inputId = cliController.askForChoice();
            selectedMode = UIMode.getModeFromId(inputId);

            if (selectedMode == null) {
                cliController.reportError("ID (" + inputId + ") non associato a nessuna modalità. Riprova.");
            }
        }

        return new Configuration(selectedMode, Locale.ITALY);
    }
}
