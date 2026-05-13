package org.dpt.auth.login.controller;

/**
 * Contratto di astrazione per l'interfaccia utente del modulo Login.
 * -
 * Definisce le operazioni di I/O necessarie per il processo di autenticazione.
 * Seguendo il pattern "Agnosticismo UI", permette al LogicController di operare
 * senza conoscere l'implementazione concreta (CLI o futura GUI), facilitando
 * la portabilità e la testabilità del sistema.
 */
public interface LoginUI {
    /** Visualizza l'intestazione grafica del modulo. */
    void showHeader();
    
    /** Mostra il menu di selezione dei ruoli. */
    void showMenu();
    
    /** Acquisisce l'ID del ruolo scelto dall'utente. */
    int askForChoice();
    
    /** Richiede l'inserimento dell'indirizzo email. */
    String askForEmail();
    
    /** Richiede l'inserimento della password (credenziale). */
    String askForPassword();
    
    /** Notifica un errore di autenticazione o validazione. */
    void reportError(String message);
    
    /** Visualizza il messaggio di uscita dal sistema. */
    void reportGoodbye();
}
