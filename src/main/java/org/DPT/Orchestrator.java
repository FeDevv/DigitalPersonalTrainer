package org.DPT;

import org.DPT.boot.controller.BootLogicController;
import org.DPT.boot.model.Configuration;
import org.DPT.boot.model.UIMode;
import org.DPT.connection.DBConnectionManager;
import org.DPT.shared.catalog.esercizi.dao.ExerciseDAO;
import org.DPT.shared.catalog.macchinari.dao.MachineDAO;
import org.DPT.shared.workout.session.dao.WorkoutSessionDAO;
import org.DPT.shared.workout.set.dao.PerformedSetDAO;
import org.DPT.shared.workout.sheet.dao.WorkoutSheetDAO;
import org.DPT.users.client.controller.ClientLogicController;
import org.DPT.users.client.dao.ClientDAO;
import org.DPT.users.login.controller.LoginLogicController;
import org.DPT.users.login.model.AuthToken;
import org.DPT.users.owner.controller.OwnerLogicController;
import org.DPT.users.pt.controller.PTLogicController;
import org.DPT.users.pt.dao.PTDAO;
import org.DPT.users.receptionist.controller.ReceptionistLogicController;

import java.util.Scanner;

/**
 * Orchestrator centrale dell'applicazione Digital Personal Trainer.
 * Gestisce il ciclo di vita globale, l'iniezione delle dipendenze (DAO condivisi)
 * e il routing dell'utente verso il modulo corretto in base al ruolo.
 */
public class Orchestrator {

    private final Scanner sharedScanner;

    // Registry dei DAO condivisi (Dependency Injection per risorse cross-modulo)
    private final PTDAO ptDAO = new PTDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final MachineDAO machineDAO = new MachineDAO();
    private final ExerciseDAO exerciseDAO = new ExerciseDAO();
    private final WorkoutSheetDAO workoutSheetDAO = new WorkoutSheetDAO();
    private final WorkoutSessionDAO workoutSessionDAO = new WorkoutSessionDAO();
    private final PerformedSetDAO performedSetDAO = new PerformedSetDAO();

    public Orchestrator() {
        // Unico punto di apertura dello Scanner di sistema
        this.sharedScanner = new Scanner(System.in);
    }

    /**
     * Avvia il flusso principale dell'applicazione.
     */
    public void run(String[] args) {
        try {
            // FASE 1: BOOTSTRAP (Configurazione UI e parametri avvio)
            BootLogicController bootController = new BootLogicController(sharedScanner);
            Configuration config = bootController.execute(args);

            if (config.uiMode() == UIMode.GUI) {
                System.out.println("\n[AVVISO] Interfaccia Grafica non ancora implementata. Riavviare in modalità CLI.");
                return;
            }

            // FASE 2: AUTHENTICATION (Handshake iniziale)
            LoginLogicController loginController = new LoginLogicController(config, sharedScanner);
            AuthToken sessionToken = loginController.execute();

            // Se l'utente ha annullato il login
            if (sessionToken == null) {
                return;
            }

            // FASE 3: DISPATCHING (Routing basato sul Ruolo RBAC)
            dispatch(config, sessionToken);

        } catch (Exception e) {
            System.err.println("\n[ERRORE DI SISTEMA] " + e.getMessage());
        } finally {
            // FASE 4: TEARDOWN (Chiusura sicura risorse)
            shutDown();
        }
    }

    /**
     * Indirizza l'utente al modulo di riferimento iniettando le dipendenze necessarie.
     */
    private void dispatch(Configuration config, AuthToken token) {
        switch (token.role()) {
            case OWNER -> {
                // Il modulo ReceptionistDAO è istanziato internamente in OwnerLogicController se necessario,
                // ma per coerenza con la DI ibrida passiamo i DAO condivisi definiti nell'Orchestrator.
                new OwnerLogicController(config, sharedScanner, token, 
                        ptDAO, new org.DPT.users.receptionist.dao.ReceptionistDAO(), clientDAO, machineDAO, exerciseDAO).execute();
            }
            case PT -> {
                new PTLogicController(config, sharedScanner, token, 
                        clientDAO, workoutSheetDAO, machineDAO, exerciseDAO).execute();
            }
            case RECEPTIONIST -> {
                new ReceptionistLogicController(config, sharedScanner, token, 
                        ptDAO, clientDAO).execute();
            }
            case CLIENT -> {
                new ClientLogicController(config, sharedScanner, token, 
                        clientDAO, workoutSheetDAO, workoutSessionDAO, performedSetDAO).execute();
            }
            default -> System.err.println("\n[ERRORE] Ruolo non riconosciuto. Impossibile avviare il modulo.");
        }
    }

    /**
     * Libera le risorse e chiude la connessione al database.
     */
    private void shutDown() {
        System.out.println("\nChiusura applicazione...");
        DBConnectionManager.getInstance().closeConnection();
        if (sharedScanner != null) {
            sharedScanner.close();
        }
        System.out.println("Risorse liberate. Arrivederci.");
    }
}
