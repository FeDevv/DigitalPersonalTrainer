package org.dpt;

/**
 * Punto di ingresso principale dell'applicazione Digital Personal Trainer.
 */
public class Main {

    static void main(String[] args) {
        Orchestrator orchestrator = new Orchestrator();
        orchestrator.run(args);
    }

}

/*
 NOTE ARCHITETTONICHE PER LO STUDIO:
 
 1. GESTIONE SELF-DEACTIVATION:
 se un addetto di segreteria si disattiva da solo, può continuare a girare nel sistema fino al riavvio,
 momento al quale non potrà fare l'accesso - locked out.
 - Scelta progettuale attuale: si privilegia la semplicità, l'utente finisce la sessione ma non può rientrare.
 
 2. LINGUA:
 Ricordarsi di controllare la regola "CODICE IN INGLESE, OUTPUT IN ITALIANO" durante la revisione riga per riga.
*/

// To resume this session: gemini --resume 6987a896-4a04-4c6c-bbf2-7ff9364b4ba8