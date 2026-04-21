package org.DPT.login.controller;

import org.DPT.exception.AuthException;
import org.DPT.exception.DatabaseException;
import org.DPT.login.model.LoginResult;
import org.DPT.login.model.UserCredentials;
import org.DPT.login.persistence.LoginDAO;
import org.DPT.persistence.connection.DBConnectionManager;

/**
 * Controller Logico per il modulo di Login.
 * Gestisce la business logic dell'autenticazione e il passaggio alla sessione di ruolo.
 * Questo componente è indipendente dall'interfaccia utente (CLI/GUI).
 */
public class LoginController {

    private final LoginDAO loginDAO;

    public LoginController() {
        this.loginDAO = new LoginDAO();
    }

    /**
     * Esegue la procedura completa di login:
     * 1. Verifica le credenziali (Handshake).
     * 2. Se valide, effettua il Role Switching sul Database.
     * 
     * @param creds Le credenziali inserite dall'utente.
     * @return L'esito del login con i dati di sessione.
     * @throws AuthException Se l'autenticazione fallisce.
     * @throws DatabaseException Se ci sono problemi tecnici di connessione.
     */
    public LoginResult login(UserCredentials creds) {
        // 1. Autenticazione (Usa l'utente dpt_login)
        LoginResult result = loginDAO.authenticate(creds);

        // 2. Role Switching (Cambia l'utente MariaDB con quello del ruolo specifico)
        // Questa operazione garantisce che da ora in poi i permessi siano quelli del ruolo.
        DBConnectionManager.getInstance().connectAs(result.role());

        return result;
    }

    /**
     * Esegue il logout chiudendo la connessione attiva.
     */
    public void logout() {
        DBConnectionManager.getInstance().closeConnection();
    }
}
