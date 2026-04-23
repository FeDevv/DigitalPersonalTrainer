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

/*
cambiare in shared "model" in "users"
revisionare il pacchetto "proprietario"
* */