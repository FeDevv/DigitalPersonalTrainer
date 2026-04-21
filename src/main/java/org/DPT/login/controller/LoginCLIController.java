package org.DPT.login.controller;

import org.DPT.auth.Role;
import org.DPT.exception.AuthException;
import org.DPT.exception.DatabaseException;
import org.DPT.exception.ValidationException;
import org.DPT.login.model.LoginResult;
import org.DPT.login.model.UserCredentials;
import org.DPT.login.view.LoginView;

import java.util.Scanner;

/**
 * Interface Controller per la versione CLI.
 * Gestisce l'input da tastiera e coordina la LoginView con il LoginController logico.
 */
public class LoginCLIController {

    private final LoginController logicController;
    private final LoginView view;
    private final Scanner scanner;

    public LoginCLIController() {
        this.logicController = new LoginController();
        this.view = new LoginView();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Avvia il ciclo di login.
     * @return Il risultato del login se ha successo, null se l'utente sceglie di uscire.
     */
    public LoginResult start() {
        view.displayHeader();

        while (true) {
            view.displayRoleMenu();
            String scelta = scanner.nextLine();

            if (scelta.equals("0")) {
                view.displayGoodbye();
                return null;
            }

            Role role = mapSelectionToRole(scelta);
            if (role == null) {
                view.displayError("Scelta non valida. Riprova.");
                continue;
            }

            try {
                LoginResult result = performLogin(role);
                view.displaySuccess(result.nomeCompleto(), result.role());
                return result;
            } catch (ValidationException | AuthException e) {
                view.displayError(e.getMessage());
            } catch (DatabaseException e) {
                view.displayError("Errore di connessione al database: " + e.getMessage());
            }
        }
    }

    private LoginResult performLogin(Role role) {
        view.promptEmail();
        String email = scanner.nextLine();

        view.promptPassword();
        String password = scanner.nextLine();

        // Creazione del record con validazione integrata
        UserCredentials creds = new UserCredentials(email, password, role);
        
        // Chiamata al controller logico (universale)
        return logicController.login(creds);
    }

    private Role mapSelectionToRole(String scelta) {
        return switch (scelta) {
            case "1" -> Role.PROPRIETARIO;
            case "2" -> Role.PT;
            case "3" -> Role.SEGRETERIA;
            case "4" -> Role.CLIENTE;
            default -> null;
        };
    }
}
