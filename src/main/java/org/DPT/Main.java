package org.DPT;

import org.DPT.login.controller.LoginCLIController;
import org.DPT.login.model.LoginResult;
import org.DPT.persistence.connection.DBConnectionManager;

/**
 * Punto di ingresso principale dell'applicazione Digital Personal Trainer.
 */
public class Main {

    public static void main(String[] args) {
        appController appController = new appController();
        appController.run();
    }
}

/*
1. attualmente il flusso di operazioni per il login è main -> LoginCLIController -> LoginController. Vorrei far si che il punto d'ingresso sia sempre
il controller logico, che in base al tipo di scelta di interfaccia, istanzia e avvia il corretto controller grafico.

2. dato che voglio una struttura "pronta ad essere espansa", direi di creare il modulo BOOT (o semplicemente implemetarlo in appController) che
manterrà tutta la logica di avvio ("scegli il tipo di interfaccia", boot dei servizi ...)

3. usiamo 1 scanner solo, lo creiamo all'inizio e facciamo dependency injection nei pacchetti che lo necessitano, ciò risulta facile in quanto abbiamo
l'elemento centrale "appController" che fa da collante tra i vari package.
*/