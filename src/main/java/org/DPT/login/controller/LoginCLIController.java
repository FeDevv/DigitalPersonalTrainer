package org.DPT.login.controller;

import org.DPT.auth.Role;
import org.DPT.login.view.LoginCLIView;

import java.util.Scanner;

/**
 * Interface Controller per la versione CLI del Login.
 */
public class LoginCLIController {

    private final LoginCLIView view;
    private final Scanner scanner;

    public LoginCLIController(Scanner scanner) {
        this.view = new LoginCLIView();
        this.scanner = scanner;
    }

    public void showHeader() {
        view.displayHeader();
    }

    public void showMenu() {
        view.displayRoleMenu(Role.values());
    }

    /**
     * Ora garantisce che il prompt "Scelta:" appaia ad ogni iterazione,
     * sia per errore di formato che per errore logico nel controller superiore.
     */
    public int askForChoice() {
        view.displayInputPrompt();

        while (!scanner.hasNextInt()) {
            view.displayError("Inserisci un formato numerico valido.");
            scanner.nextLine(); 
            view.displayInputPrompt();
        }

        int choice = scanner.nextInt();
        scanner.nextLine(); 
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
