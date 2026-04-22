package org.DPT.login.controller;

import org.DPT.auth.Role;
import org.DPT.boot.model.Configuration;
import org.DPT.boot.model.UIMode;
import org.DPT.exception.AuthException;
import org.DPT.exception.DatabaseException;
import org.DPT.exception.ValidationException;
import org.DPT.login.model.LoginResult;
import org.DPT.login.model.UserCredentials;
import org.DPT.login.persistence.LoginDAO;
import org.DPT.persistence.connection.DBConnectionManager;

import java.util.Scanner;

/**
 * Controller Logico (Core) per il modulo di Login.
 */
public class LoginController {

    private final LoginDAO loginDAO;
    private final Configuration config;
    private final Scanner sharedScanner;

    public LoginController(Configuration config, Scanner sharedScanner) {
        this.config = config;
        this.sharedScanner = sharedScanner;
        this.loginDAO = new LoginDAO();
    }

    public LoginResult execute() {
        if (config.uiMode() == UIMode.CLI) {
            return runCLISequence();
        } else {
            throw new UnsupportedOperationException("GUI non ancora implementata.");
        }
    }

    private LoginResult runCLISequence() {
        LoginCLIController cliController = new LoginCLIController(sharedScanner);

        cliController.showHeader();
        cliController.showMenu(); // Stampato una volta sola prima del ciclo

        while (true) {
            int choice = cliController.askForChoice();

            if (choice == 0) {
                cliController.reportGoodbye();
                return null;
            }

            Role selectedRole = Role.getRoleFromId(choice);
            if (selectedRole == null || selectedRole == Role.LOGIN) {
                cliController.reportError("ID (" + choice + ") non valido. Riprova.");
                continue; // Ritorna ad askForChoice() senza ristampare il menu
            }

            String email = cliController.askForEmail();
            String password = cliController.askForPassword();

            try {
                UserCredentials creds = new UserCredentials(email, password, selectedRole);
                LoginResult result = loginDAO.authenticate(creds);
                DBConnectionManager.getInstance().connectAs(result.role());

                cliController.reportSuccess(result.nomeCompleto(), result.role());
                return result;

            } catch (ValidationException | AuthException e) {
                cliController.reportError(e.getMessage());
                cliController.showMenu(); // In caso di credenziali errate, ha senso riproporre il menu ruoli? 
                // Seguendo la tua richiesta, se vuoi che sia proprio identico a boot, 
                // potremmo anche qui non riproporlo, ma solitamente dopo un errore di login 
                // l'utente potrebbe voler cambiare ruolo. Per ora lo lasciamo "pulito" come richiesto.
            } catch (DatabaseException e) {
                cliController.reportError("Problema tecnico: " + e.getMessage());
            } catch (Exception e) {
                cliController.reportError("Errore imprevisto: " + e.getMessage());
            }
        }
    }

    public void logout() {
        DBConnectionManager.getInstance().closeConnection();
    }
}
