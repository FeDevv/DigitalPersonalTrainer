package org.DPT;

import org.DPT.login.controller.LoginCLIController;
import org.DPT.login.model.LoginResult;
import org.DPT.persistence.connection.DBConnectionManager;

/**
 * Punto di ingresso principale dell'applicazione Digital Personal Trainer.
 */
public class Main {

    public static void main(String[] args) {
        Orchestrator orchestrator = new Orchestrator();
        orchestrator.run(args);
    }

}
