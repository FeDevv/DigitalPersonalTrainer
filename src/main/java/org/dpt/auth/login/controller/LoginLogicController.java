package org.dpt.auth.login.controller;

import org.dpt.auth.Role;
import org.dpt.shared.mvc.ControllerContext;
import org.dpt.exception.AuthException;
import org.dpt.exception.DatabaseException;
import org.dpt.exception.ValidationException;
import org.dpt.auth.login.factory.LoginUIFactory;
import org.dpt.auth.login.model.AuthToken;
import org.dpt.auth.login.model.UserCredentials;
import org.dpt.auth.login.dao.LoginDAO;
import org.dpt.connection.DBConnectionManager;

/**
 * Controller logico responsabile dell'orchestrazione del processo di autenticazione.
 * -
 * Agisce come intermediario tra l'interfaccia di login (UI) e il Data Access Object (DAO),
 * gestendo il flusso di controllo che permette a un utente di identificarsi nel sistema.
 * -
 * Il controller implementa la logica di switching post-autenticazione: una volta 
 * validate le credenziali, coordina con il DBConnectionManager il passaggio dalla 
 * connessione tecnica di 'login' a quella operativa specifica per il ruolo ottenuto.
 */
public class LoginLogicController {

    private final LoginDAO loginDAO;
    private final LoginUI ui;

    /**
     * Inizializza il modulo di login iniettando le dipendenze necessarie.
     * @param ctx Il contesto di esecuzione (contiene la connessione tecnica 'login').
     */
    public LoginLogicController(ControllerContext ctx) {
        this.loginDAO = new LoginDAO(ctx.connection());
        this.ui = LoginUIFactory.getUI(ctx.config().uiMode(), ctx.scanner());
    }

    /**
     * Avvia e gestisce il ciclo interattivo di autenticazione.
     * -
     * Il metodo presenta all'utente la scelta del ruolo e richiede le credenziali.
     * Gestisce i tentativi falliti e le eccezioni di sicurezza, fornendo feedback
     * appropriato tramite la UI.
     * 
     * @return Un AuthToken valido in caso di successo, null se l'utente sceglie di uscire.
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
                ui.reportError("Selezione non valida: il ruolo ID " + choice + " non esiste o non è ammesso al login.");
                continue;
            }

            String email = ui.askForEmail();
            String password = ui.askForPassword();

            try {
                // Impacchettamento credenziali per la validazione atomica
                UserCredentials creds = new UserCredentials(email, password, selectedRole);
                
                // Fase 1: Verifica identità e stato (Active Check)
                AuthToken result = loginDAO.authenticate(creds);
                
                // Fase 2: Role Switching (Elevazione privilegi JDBC)
                DBConnectionManager.getInstance().connectAs(result.role());

                return result;

            } catch (ValidationException | AuthException e) {
                // Gestione controllata degli errori di accesso o formato
                ui.reportError(e.getMessage());
                ui.showMenu();
            } catch (DatabaseException e) {
                ui.reportError("Errore critico di comunicazione con il database: " + e.getMessage());
            } catch (Exception e) {
                ui.reportError("Anomalia imprevista durante il login: " + e.getMessage());
            }
        }
    }
}
