package org.DPT.boot.controller;

import org.DPT.boot.model.UIMode;
import org.DPT.boot.view.BootCLIView;

import java.util.Scanner;

/**
 * Gestisce il flusso dell'interfaccia CLI.
 * Legge l'input tramite lo Scanner condiviso e comanda la View per l'output.
 */
public class BootCLIController {

    private final Scanner scanner;
    private final BootCLIView view;

    // Riceve lo scanner dal controller logico
    public BootCLIController(Scanner scanner) {
        this.scanner = scanner;
        this.view = new BootCLIView();
    }

    /**
     * Pilota la View per mostrare il menu e gestisce l'acquisizione sicura dell'intero.
     */
    public int askForUIModeSelection() {
        view.displayWelcome();
        // Passa l'elenco dinamico alla view affinché lo stampi
        view.displayMenu(UIMode.values());
        view.displayInputPrompt();

        // Validazione formale dell'input (deve essere un numero)
        while (!scanner.hasNextInt()) {
            view.displayError("Inserisci un formato numerico valido.");
            view.displayInputPrompt();
            scanner.next(); // Consuma il buffer errato
        }

        return scanner.nextInt();
    }

    /**
     * Metodo ponte affinché il controller logico possa scatenare un errore visivo.
     */
    public void reportValidationError(String message) {
        view.displayError(message);
    }
}
