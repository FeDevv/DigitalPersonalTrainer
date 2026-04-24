package org.DPT.shared.ui;

import java.util.Scanner;

/**
 * Controller base per tutte le interfacce CLI.
 * Implementa la logica di acquisizione dati robusta (DRY).
 */
public abstract class BaseCLIController {

    protected final Scanner scanner;
    protected final BaseCLIView view;

    protected BaseCLIController(Scanner scanner, BaseCLIView view) {
        this.scanner = scanner;
        this.view = view;
    }

    /**
     * Legge un intero con gestione dell'errore di formato.
     */
    protected int readInt(String prompt) {
        view.displayInputPrompt(prompt);
        while (!scanner.hasNextInt()) {
            view.displayError("Inserisci un formato numerico valido.");
            scanner.nextLine(); // Pulisce il buffer
            view.displayInputPrompt(prompt);
        }
        int val = scanner.nextInt();
        scanner.nextLine(); // Consuma il newline
        return val;
    }

    /**
     * Legge una stringa non vuota.
     */
    protected String readString(String label) {
        view.displayLabel(label);
        String input = scanner.nextLine().trim();
        while (input.isEmpty()) {
            view.displayError("Il campo non può essere vuoto.");
            view.displayLabel(label);
            input = scanner.nextLine().trim();
        }
        return input;
    }

    public void reportError(String message) {
        view.displayError(message);
    }
}
