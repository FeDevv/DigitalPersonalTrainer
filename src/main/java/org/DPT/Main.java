package org.DPT;

import org.DPT.dirty.Orchestrator;

/**
 * Punto di ingresso principale dell'applicazione Digital Personal Trainer.
 */
public class Main {

    public static void main(String[] args) {
        Orchestrator orchestrator = new Orchestrator();
        orchestrator.run(args);
    }

}

/*
il package "dirty" contiene tutti i pacchetti/file che non rispettano gli standard previsti,
vanno refattorizzati e/o completati affinchè rispettino gli standard.
*/

// mancano i moduli 'client' e 'pt'