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

// rivedere l'orchestrator

// To resume this session: gemini --resume 859b85b4-6500-488d-a58b-18d3dc4cada6

// aggiungere al boot la modalità CLI color, dove creiamo un'altri elementi in shared.ui che sono
// identici a quelli ora presenti, ma con i colori per CLI, l'orchestrator (o il boot) semplicemente
// andrà ad istanziare il blocco corretto

// clientLogicController linea 148 - spiegazione

// possibilmente rimuovere da boot.model.Configuration il "Locale" in quanto mai usato

// record in PTUI da spostare in model?