package org.DPT.login.controller;

import org.DPT.auth.Role;
import org.DPT.exception.AuthException;
import org.DPT.exception.DatabaseException;
import org.DPT.exception.ValidationException;
import org.DPT.login.model.LoginResult;
import org.DPT.login.model.UserCredentials;
import org.DPT.login.view.LoginCLIView;

import java.util.Scanner;

/**
 * Interface Controller per la versione CLI del Login.
 * Si occupa SOLO di I/O: legge input utente e comanda la View per l'output.
 * Nessuna regola di business, nessuna gestione eccezioni di dominio.
 */
public class LoginCLIController {

    private final LoginCLIView view;
    private final Scanner scanner;

    // Dependency Injection: lo Scanner arriva dal controller logico (condiviso dall'Orchestrator)
    public LoginCLIController(Scanner scanner) {
        this.view = new LoginCLIView();
        this.scanner = scanner;
    }

    public void showHeader() {
        view.displayHeader();
    }

    /**
     * Pilota la View per mostrare il menu dei ruoli e legge un ID in modo sicuro.
     * Gestisce la pulizia del buffer e gli errori di formato (lettere invece di numeri).
     */
    public int askForRoleSelection() {
        view.displayRoleMenu(Role.values());

        while (!scanner.hasNextInt()) {
            view.displayError("Inserisci un formato numerico valido.");
            scanner.nextLine(); // Consuma input errato
            view.displayRoleMenu(Role.values()); // Ristampa il menu pulito
        }

        int choice = scanner.nextInt();
        scanner.nextLine(); // PULIZIA BUFFER (consuma il newline)
        return choice;
    }

    public String askForEmail() {
        view.promptEmail();
        return scanner.nextLine().trim();
    }

    public String askForPassword() {
        view.promptPassword();
        return scanner.nextLine().trim();
    }

    // Metodi ponte per permettere al controller logico di stampare esiti
    public void reportError(String message) {
        view.displayError(message);
    }

    public void reportSuccess(String nomeCompleto, Role role) {
        view.displaySuccess(nomeCompleto, role);
    }

    public void reportGoodbye() {
        view.displayGoodbye();
    }
}
