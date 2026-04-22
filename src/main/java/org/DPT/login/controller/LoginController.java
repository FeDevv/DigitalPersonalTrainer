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
 * Dirige il flusso dell'autenticazione. In base alla configurazione avvia la CLI o la GUI.
 */
public class LoginController {

    private final LoginDAO loginDAO;
    private final Configuration config;
    private final Scanner sharedScanner; // Presente se in modalità CLI

    public LoginController(Configuration config, Scanner sharedScanner) {
        this.config = config;
        this.sharedScanner = sharedScanner;
        this.loginDAO = new LoginDAO();
    }

    /**
     * Punto d'ingresso del modulo. Sceglie il ramo visivo corretto.
     * @return LoginResult se loggato, null se l'utente sceglie "Esci".
     */
    public LoginResult execute() {
        if (config.uiMode() == UIMode.CLI) {
            return runCLISequence();
        } else {
            // runGUISequence(); // Da implementare in futuro con JavaFX
            throw new UnsupportedOperationException("GUI non ancora implementata.");
        }
    }

    /**
     * Il vero e proprio ciclo di business logico per la CLI.
     */
    private LoginResult runCLISequence() {
        LoginCLIController cliController = new LoginCLIController(sharedScanner);

        cliController.showHeader();

        while (true) {
            // 1. Chiede l'input alla grafica
            int choice = cliController.askForRoleSelection();

            // 2. Condizione di uscita
            if (choice == 0) {
                cliController.reportGoodbye();
                return null;
            }

            // 3. Mapping ID -> Ruolo
            Role selectedRole = Role.getRoleFromId(choice);
            if (selectedRole == null || selectedRole == Role.LOGIN) {
                cliController.reportError("Scelta non valida. Riprova.");
                continue;
            }

            // 4. Raccolta credenziali sensibili
            String email = cliController.askForEmail();
            String password = cliController.askForPassword();

            // 5. Tentativo di Login e Role Switching
            try {
                // Il costruttore del record fa scattare la ValidationException se i dati sono vuoti
                UserCredentials creds = new UserCredentials(email, password, selectedRole);

                // Chiamata al DAO (Usa l'utente DB di default: dpt_login)
                LoginResult result = loginDAO.authenticate(creds);

                // IMPORTANTE: Cambio utente sul Database! Da ora la connessione ha i GRANT di questo ruolo.
                DBConnectionManager.getInstance().connectAs(result.role());

                // Se arriviamo qui, è andato tutto bene
                cliController.reportSuccess(result.nomeCompleto(), result.role());
                return result;

            } catch (ValidationException | AuthException e) {
                // Errori di dominio (es. password errata, formato email errato)
                cliController.reportError(e.getMessage());
            } catch (DatabaseException e) { // O SQLException a seconda di come hai fatto
                // Errori di sistema (es. database spento)
                cliController.reportError("Problema tecnico di connessione: " + e.getMessage());
            } catch (Exception e) {
                // Catch all difensivo per non far crashare mai l'app
                cliController.reportError("Errore imprevisto: " + e.getMessage());
            }
        }
    }

    /**
     * Da chiamare quando l'utente clicca "Logout" nella dashboard principale.
     */
    public void logout() {
        DBConnectionManager.getInstance().closeConnection();
    }
}
