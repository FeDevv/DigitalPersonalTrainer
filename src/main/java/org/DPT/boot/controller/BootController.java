package org.DPT.boot.controller;

import org.DPT.boot.model.Configuration;
import org.DPT.boot.model.UIMode;

import java.util.Locale;
import java.util.Scanner;

/**
 * Il Controller Logico Principale del Boot.
 * Unico punto di contatto per l'Orchestrator.
 */
public class BootController {

    private final BootCLIController cliController;

    // L'Orchestrator inietta lo Scanner qui
    public BootController(Scanner sharedScanner) {
        // Il controller logico avvia quello grafico passandogli lo scanner
        this.cliController = new BootCLIController(sharedScanner);
    }

    /**
     * Esegue la logica di avvio.
     * @param args Array di argomenti passati al main
     * @return Configuration popolata
     */
    public Configuration execute(String[] args) {
        // 1. Fase Silenziosa: elaborazione prioritaria degli argomenti CLI
        if (args != null && args.length > 0) {
            for (String arg : args) {
                if (arg.equalsIgnoreCase("--gui")) return Configuration.defaultGUI();
                if (arg.equalsIgnoreCase("--cli")) return Configuration.defaultCLI();
            }
        }

        // 2. Fase Interattiva: delega l'acquisizione dati al controller I/O
        cliController.showWelcome(); // Visualizzato solo una volta all'inizio della fase interattiva
        UIMode selectedMode = null;

        while (selectedMode == null) {
            // Ottiene il numero inserito
            int inputId = cliController.askForUIModeSelection();

            // Logica di dominio: decodifica l'ID nell'Enum
            selectedMode = UIMode.getModeFromId(inputId);

            // Se l'ID non esiste nell'Enum, segnala l'errore logico al controller UI
            if (selectedMode == null) {
                cliController.reportValidationError("ID (" + inputId + ") non associato a nessuna modalità. Riprova.");
            }
        }

        // 3. Creazione e restituzione del "vassoio" dati
        return new Configuration(selectedMode, Locale.ITALY);
    }
}
