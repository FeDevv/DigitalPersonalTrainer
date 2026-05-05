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

// enum role in shared.auth necessitava dei nomi in italiano per rispecchiare quelli in db.properties
// l'utente client in schema.sql non aveva i permessi di select e lettura delle tabelle per l'accesso
// pensare al logging (aggiungere un logger)