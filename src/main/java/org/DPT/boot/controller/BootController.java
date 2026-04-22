package org.DPT.boot.controller;

import org.DPT.boot.model.Configuration;
import org.DPT.boot.model.UIMode;

import java.util.Locale;
import java.util.Scanner;

/**
 * Il Controller Logico Principale del Boot.
 */
public class BootController {

    private final BootCLIController cliController;

    public BootController(Scanner sharedScanner) {
        this.cliController = new BootCLIController(sharedScanner);
    }

    public Configuration execute(String[] args) {
        if (args != null && args.length > 0) {
            for (String arg : args) {
                if (arg.equalsIgnoreCase("--gui")) return Configuration.defaultGUI();
                if (arg.equalsIgnoreCase("--cli")) return Configuration.defaultCLI();
            }
        }

        cliController.showWelcome();
        cliController.showMenu(); // Stampato una volta sola
        
        UIMode selectedMode = null;

        while (selectedMode == null) {
            int inputId = cliController.askForChoice();
            selectedMode = UIMode.getModeFromId(inputId);

            if (selectedMode == null) {
                cliController.reportValidationError("ID (" + inputId + ") non associato a nessuna modalità. Riprova.");
            }
        }

        return new Configuration(selectedMode, Locale.ITALY);
    }
}
