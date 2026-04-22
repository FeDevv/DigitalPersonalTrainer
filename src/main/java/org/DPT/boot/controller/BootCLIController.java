package org.DPT.boot.controller;

import org.DPT.boot.model.UIMode;
import org.DPT.boot.view.BootCLIView;

import java.util.Scanner;

/**
 * Gestisce il flusso dell'interfaccia CLI per il boot.
 */
public class BootCLIController {

    private final Scanner scanner;
    private final BootCLIView view;

    public BootCLIController(Scanner scanner) {
        this.scanner = scanner;
        this.view = new BootCLIView();
    }

    public void showWelcome() {
        view.displayWelcome();
    }

    public void showMenu() {
        view.displayMenu(UIMode.values());
    }

    /**
     * Chiamata ad ogni tentativo di acquisizione ID.
     */
    public int askForChoice() {
        view.displayInputPrompt(); // Ora viene mostrato SEMPRE, anche dopo errore logico

        while (!scanner.hasNextInt()) {
            view.displayError("Inserisci un formato numerico valido.");
            scanner.nextLine(); 
            view.displayInputPrompt();
        }

        int choice = scanner.nextInt();
        scanner.nextLine(); 

        return choice;
    }

    public void reportValidationError(String message) {
        view.displayError(message);
    }
}
