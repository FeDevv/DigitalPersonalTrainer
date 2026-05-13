package org.dpt.shared.mvc;

import org.dpt.exception.DPTException;

/**
 * Fondamenta architetturale per la logica di controllo di tutti i moduli del sistema.
 * 
 * Implementa il pattern "Template Method" (GoF) per definire l'algoritmo invariante
 * del ciclo di vita di una sessione utente (Heartbeat -> Render Menu -> Choice -> Execution).
 * Questo approccio garantisce una gestione centralizzata e robusta degli errori,
 * del logging di logout e della verifica dell'integrità dell'utenza (account disattivati),
 * riducendo drasticamente il boilerplate nei LogicController specifici.
 */
public abstract class AbstractLogicController {
    
    /** Il contesto di esecuzione condiviso che aggrega dipendenze e stato della sessione. */
    protected final ControllerContext context;
    
    /** Flag di controllo per la terminazione del loop di esecuzione. */
    protected boolean logout = false;

    /**
     * Inizializza il controller con il contesto necessario.
     * @param context Il record contenente configurazione, scanner, token e connessione JDBC.
     */
    protected AbstractLogicController(ControllerContext context) {
        this.context = context;
    }

    /**
     * Il Template Method: definisce lo scheletro dell'esecuzione della sessione.
     * È dichiarato final per garantire l'immutabilità del ciclo di vita globale
     * e prevenire violazioni dell'architettura da parte delle sottoclassi.
     * -
     * Implementa un meccanismo di recupero dagli errori che cattura sia le eccezioni 
     * di dominio (DPTException) che quelle impreviste, assicurando che un errore
     * in una singola operazione non provochi il crash dell'intero modulo.
     */
    public final void execute() {
        showHeader();
        
        while (!logout) {
            try {
                // Heartbeat: verifica dinamica della validità dell'account lato DB
                if (!isUserActive()) {
                    reportError("Il tuo account è stato disattivato dall'amministrazione. Verrai disconnesso.");
                    logout = true;
                    continue;
                }

                renderMenu();
                
                int choice = askForChoice();
                
                if (choice == 0) {
                    logout = true;
                } else {
                    handleChoice(choice);
                }
                
            } catch (DPTException e) {
                // Fall-back controllato su errori di business logic (es. validazione fallita)
                reportError(e.getMessage());
            } catch (RuntimeException e) {
                // Gestione di sicurezza per errori tecnici inaspettati
                reportError("Anomalia di sistema: " + e.getMessage());
            }
        }
        
        onLogout();
    }

    /** Verifica se l'utente autenticato è ancora abilitato ad operare. */
    protected abstract boolean isUserActive();
    
    /** Visualizza l'header specifico del modulo (es. "Modulo PT - [Nome]"). */
    protected abstract void showHeader();
    
    /** Renderizza le opzioni disponibili nel menu principale del modulo. */
    protected abstract void renderMenu();
    
    /** Acquisisce in modo sicuro la scelta dell'utente tramite la UI associata. */
    protected abstract int askForChoice();
    
    /** Dispatcher interno per inoltrare la richiesta ai metodi di business specifici. */
    protected abstract void handleChoice(int choice);
    
    /** Operazioni di cleanup e saluto alla chiusura del modulo. */
    protected abstract void onLogout();
    
    /** Notifica un errore all'utente utilizzando il canale di output configurato. */
    protected abstract void reportError(String message);
}
