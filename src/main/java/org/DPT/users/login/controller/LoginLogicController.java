package org.DPT.users.login.controller;

import org.DPT.shared.auth.Role;
import org.DPT.boot.model.Configuration;
import org.DPT.exception.AuthException;
import org.DPT.exception.DatabaseException;
import org.DPT.exception.ValidationException;
import org.DPT.users.login.factory.LoginUIFactory;
import org.DPT.users.login.model.LoginResult;
import org.DPT.users.login.model.UserCredentials;
import org.DPT.users.login.dao.LoginDAO;
import org.DPT.connection.DBConnectionManager;

import java.util.Scanner;

/**
 * Controller Logico (Core) per il modulo di Login.
 * Gestisce il coordinamento tra l'interfaccia utente (agnostica) e la logica di autenticazione.
 */
public class LoginLogicController {

    private final LoginDAO loginDAO;
    private final LoginUI ui;

    public LoginLogicController(Configuration config, Scanner sharedScanner) {
        this.loginDAO = new LoginDAO();
        // L'interfaccia viene istanziata tramite la factory locale
        this.ui = LoginUIFactory.getUI(config.uiMode(), sharedScanner);
    }

    /**
     * Esegue la sequenza di login.
     * @return LoginResult con i dati di sessione o null se l'utente esce.
     */
    public LoginResult execute() {
        ui.showHeader();
        ui.showMenu();

        while (true) {
            int choice = ui.askForChoice();

            if (choice == 0) {
                ui.reportGoodbye();
                return null;
            }

            Role selectedRole = Role.getRoleFromId(choice);
            if (selectedRole == null || selectedRole == Role.LOGIN) {
                ui.reportError("ID selezionato (" + choice + ") non valido.");
                continue;
            }

            String email = ui.askForEmail();
            String password = ui.askForPassword();

            try {
                UserCredentials creds = new UserCredentials(email, password, selectedRole);
                
                LoginResult result = loginDAO.authenticate(creds);
                DBConnectionManager.getInstance().connectAs(result.role());

                ui.reportSuccess(result.nomeCompleto(), result.role());
                return result;

            } catch (ValidationException | AuthException e) {
                ui.reportError(e.getMessage());
                ui.showMenu();
            } catch (DatabaseException e) {
                ui.reportError("Errore Database: " + e.getMessage());
            } catch (Exception e) {
                ui.reportError("Errore imprevisto: " + e.getMessage());
            }
        }
    }

    public void logout() {
        DBConnectionManager.getInstance().closeConnection();
    }
}
