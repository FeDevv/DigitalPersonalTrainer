package org.DPT;

/**
 * Punto di ingresso principale dell'applicazione Digital Personal Trainer.
 */
public class Main {

    public static void main(String[] args) {
        Orchestrator orchestrator = new Orchestrator();
        orchestrator.run(args);
    }

}

// TODO: Semplificare l'Enum ROLE in shared.auth rimuovendo 'configKey'. 
// Allineare i nomi utenti in schema.sql e le chiavi in db.properties alle costanti dell'enum in inglese 
// (es. OWNER, PT, RECEPTIONIST, CLIENT) per rispettare lo standard "codice in inglese, output in italiano" 
// ed eliminare l'accoppiamento hardcoded introdotto come fix temporaneo.


// l'utente client in schema.sql non aveva i permessi di select e lettura delle tabelle per l'accesso
// pensare al logging (aggiungere un logger)