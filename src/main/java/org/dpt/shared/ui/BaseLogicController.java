package org.dpt.shared.ui;

import org.dpt.exception.DPTException;
import org.dpt.shared.context.ControllerContext;

/**
 * Classe base astratta per tutti i LogicController del sistema.
 * Implementa il pattern "Template Method" per centralizzare il loop della sessione
 * e la gestione degli errori, riducendo il boilerplate nei moduli specifici.
 */
public abstract class BaseLogicController {
    
    protected final ControllerContext context;
    protected boolean logout = false;

    protected BaseLogicController(ControllerContext context) {
        this.context = context;
    }

    /**
     * Il Template Method: definisce lo scheletro dell'esecuzione.
     * È final per impedire ai figli di rompere il ciclo di vita standard.
     */
    public final void execute() {
        showHeader();
        
        while (!logout) {
            try {
                // Heartbeat: verifica se l'utenza è ancora attiva
                if (!isUserActive()) {
                    reportError("Il tuo account è stato disattivato. Verrai disconnesso dal sistema.");
                    logout = true;
                    continue;
                }

                // 1. Mostra il menu specifico
                renderMenu();
                
                // 2. Acquisisce la scelta
                int choice = askForChoice();
                
                // 3. Gestisce l'uscita o l'azione
                if (choice == 0) {
                    logout = true;
                } else {
                    handleChoice(choice);
                }
                
            } catch (DPTException e) {
                // Gestione specifica delle eccezioni di dominio (previste)
                reportError(e.getMessage());
            } catch (RuntimeException e) {
                // Gestione degli errori imprevisti (bug o problemi tecnici seri)
                reportError("Errore imprevisto del sistema: " + e.getMessage());
            }
        }
        
        onLogout();
    }

    // Metodi che le sottoclassi DEVONO implementare
    protected abstract boolean isUserActive();
    protected abstract void showHeader();
    protected abstract void renderMenu();
    protected abstract int askForChoice();
    protected abstract void handleChoice(int choice);
    protected abstract void onLogout();
    protected abstract void reportError(String message);
}
