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
email regex in user.login.usercredentials; EMAIL_REGEX debba stare qui o in una classe
di utilità in shared.util (es. ValidationUtils), nel caso servisse validare email
anche altrove (es. nella creazione di un nuovo PT nel modulo Proprietario).
*/

/*
il package "dirty" contiene tutti i pacchetti/file che non rispettano gli standard previsti,
vanno refattorizzati e/o completati affinchè rispettino gli standard.
*/