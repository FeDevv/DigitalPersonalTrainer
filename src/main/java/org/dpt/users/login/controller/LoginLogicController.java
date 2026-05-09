package org.dpt.users.login.controller;

import org.dpt.shared.auth.Role;
import org.dpt.boot.model.Configuration;
import org.dpt.exception.AuthException;
import org.dpt.exception.DatabaseException;
import org.dpt.exception.ValidationException;
import org.dpt.users.login.factory.LoginUIFactory;
import org.dpt.users.login.model.AuthToken;
import org.dpt.users.login.model.UserCredentials;
import org.dpt.users.login.dao.LoginDAO;
import org.dpt.connection.DBConnectionManager;

import java.sql.Connection;
import java.util.Scanner;

/**
 * Controller Logico (Core) per il modulo di Login.
 * Gestisce il coordinamento tra l'interfaccia utente (agnostica) e la logica di autenticazione.
 */
public class LoginLogicController {

    private final LoginDAO loginDAO;
    private final LoginUI ui;

    public LoginLogicController(Configuration config, Scanner sharedScanner, Connection conn) {
        this.loginDAO = new LoginDAO(conn);
        // L'interfaccia viene istanziata tramite la factory locale
        this.ui = LoginUIFactory.getUI(config.uiMode(), sharedScanner);
    }

    /**
     * Esegue la sequenza di login.
     * @return LoginResult con i dati di sessione o null se l'utente esce.
     */
    public AuthToken execute() {
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
                
                AuthToken result = loginDAO.authenticate(creds);
                DBConnectionManager.getInstance().connectAs(result.role());

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
}
