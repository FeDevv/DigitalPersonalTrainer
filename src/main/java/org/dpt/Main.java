package org.dpt;

/**
 * Punto di ingresso principale dell'applicazione Digital Personal Trainer.
 * -
 * Questa classe ha il compito di avviare l'intero ecosistema applicativo 
 * istanziando l'Orchestrator centrale, che prenderà il controllo del 
 * flusso di esecuzione e della gestione dei moduli.
 */
public class Main {

    /**
     * Metodo di avvio (entry point).
     * 
     * @param args Argomenti della riga di comando passati all'applicazione.
     */
    static void main(String[] args) {
        Orchestrator orchestrator = new Orchestrator();
        orchestrator.run(args);
    }

}