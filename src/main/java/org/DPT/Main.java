package org.DPT;

/**
 * Punto di ingresso principale dell'applicazione Digital Personal Trainer.
 */
public class Main {

    static void main(String[] args) {
        Orchestrator orchestrator = new Orchestrator();
        orchestrator.run(args);
    }

}

// rivedere l'orchestrator

// aggiungere al boot la modalità CLI color, dove creiamo un'altri elementi in shared.ui che sono
// identici a quelli ora presenti, ma con i colori per CLI, l'orchestrator (o il boot) semplicemente
// andrà ad istanziare il blocco corretto

// clientLogicController linea 148 - spiegazione