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

// riscrivendo i commenti

// To resume this session: gemini --resume c4c91b77-3068-44e0-9f6f-358fbe6e0d56
/*
* Packages DONE
* - connection
* - shared
* - auth
* - boot
* */

// domain - ricontrollare se i commenti in workout sono corretti. Teoricamente domain.catalog e domain.user sono ben commentati.

// duplicazione PTCLIView e ClientCLIView - displaySheetDetails e displayActiveRoutine rispettivamente