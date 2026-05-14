package org.dpt;

import org.dpt.boot.controller.BootLogicController;
import org.dpt.boot.model.Configuration;
import org.dpt.boot.model.UIMode;
import org.dpt.connection.DBConnectionManager;
import org.dpt.auth.Role;
import org.dpt.domain.catalog.exercise.dao.ExerciseDAO;
import org.dpt.domain.catalog.machine.dao.MachineDAO;
import org.dpt.shared.mvc.ControllerContext;
import org.dpt.shared.mvc.AbstractCLIView;
import org.dpt.domain.workout.session.dao.WorkoutSessionDAO;
import org.dpt.domain.workout.set.dao.PerformedSetDAO;
import org.dpt.domain.workout.sheet.dao.WorkoutSheetDAO;
import org.dpt.user.client.controller.ClientLogicController;
import org.dpt.user.client.dao.ClientDAO;
import org.dpt.auth.login.controller.LoginLogicController;
import org.dpt.auth.login.model.AuthToken;
import org.dpt.user.owner.controller.OwnerLogicController;
import org.dpt.user.pt.controller.PTLogicController;
import org.dpt.user.pt.dao.PTDAO;
import org.dpt.user.receptionist.controller.ReceptionistLogicController;
import org.dpt.user.receptionist.dao.ReceptionistDAO;

import java.sql.Connection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Componente centrale di governo dell'applicazione Digital Personal Trainer.
 * -
 * Agisce come "Regista" (Orchestrator) dell'intero sistema, essendo responsabile 
 * del coordinamento dei moduli, della gestione del ciclo di vita globale della 
 * sessione e della corretta inizializzazione delle dipendenze condivise.
 * -
 * L'architettura dell'Orchestrator si basa su:
 * <ul>
 *   <li><b>Strategy Pattern:</b> Utilizza una mappa di dispatching per caricare i moduli
 *       in base al ruolo dell'utente, garantendo l'Open/Closed Principle.</li>
 *   <li><b>Dependency Injection Manuale:</b> Gestisce l'istanziazione e l'iniezione 
 *       dei DAO e dei Controller, centralizzando la configurazione del grafo degli oggetti.</li>
 *   <li><b>Session Lifecycle Management:</b> Governa il passaggio dalla fase di Boot 
 *       all'Handshake (Login) e infine allo Switching operativo.</li>
 * </ul>
 */
public class Orchestrator {

    private final Scanner sharedScanner;
    private final AbstractCLIView view;

    /**
     * Interfaccia funzionale (Command Pattern) per il caricamento dinamico dei moduli.
     */
    @FunctionalInterface
    private interface ModuleLauncher {
        /**
         * Inizializza e avvia il LogicController associato al ruolo.
         * @param config Configurazione di boot.
         * @param token Token di sessione ottenuto in fase di login.
         */
        void launch(Configuration config, AuthToken token);
    }

    /** Registro delle strategie di lancio per ogni ruolo autorizzativo. */
    private final Map<Role, ModuleLauncher> dispatchMap = new EnumMap<>(Role.class);

    /**
     * Inizializza l'orchestratore preparando lo scanner di sistema e la view di base.
     */
    public Orchestrator() {
        this.sharedScanner = new Scanner(System.in);
        this.view = new AbstractCLIView();
    }

    /**
     * Configura il grafo delle dipendenze per ogni modulo funzionale.
     * -
     * In questo metodo avviene il coordinamento dei DAO: quelli locali al modulo 
     * vengono istanziati direttamente, mentre quelli esterni (cross-module) vengono 
     * iniettati per mantenere il disaccoppiamento tra i pacchetti.
     * 
     * @param conn La connessione JDBC attiva con i privilegi del ruolo corrente.
     */
    private void initializeDispatchMap(Connection conn) {
        dispatchMap.put(Role.OWNER, (config, token) -> {
            ControllerContext ctx = new ControllerContext(config, sharedScanner, token, conn);
            PTDAO ptDAO = new PTDAO(conn);
            ReceptionistDAO receptionistDAO = new ReceptionistDAO(conn);
            ClientDAO clientDAO = new ClientDAO(conn);
            MachineDAO machineDAO = new MachineDAO(conn);
            ExerciseDAO exerciseDAO = new ExerciseDAO(conn);
            new OwnerLogicController(ctx, ptDAO, receptionistDAO, clientDAO, machineDAO, exerciseDAO).execute();
        });

        dispatchMap.put(Role.PT, (config, token) -> {
            ControllerContext ctx = new ControllerContext(config, sharedScanner, token, conn);
            ClientDAO clientDAO = new ClientDAO(conn);
            WorkoutSheetDAO sheetDAO = new WorkoutSheetDAO(conn);
            MachineDAO machineDAO = new MachineDAO(conn);
            ExerciseDAO exerciseDAO = new ExerciseDAO(conn);
            new PTLogicController(ctx, clientDAO, sheetDAO, machineDAO, exerciseDAO).execute();
        });

        dispatchMap.put(Role.RECEPTIONIST, (config, token) -> {
            ControllerContext ctx = new ControllerContext(config, sharedScanner, token, conn);
            PTDAO ptDAO = new PTDAO(conn);
            ClientDAO clientDAO = new ClientDAO(conn);
            new ReceptionistLogicController(ctx, ptDAO, clientDAO).execute();
        });

        dispatchMap.put(Role.CLIENT, (config, token) -> {
            ControllerContext ctx = new ControllerContext(config, sharedScanner, token, conn);
            WorkoutSheetDAO sheetDAO = new WorkoutSheetDAO(conn);
            WorkoutSessionDAO sessionDAO = new WorkoutSessionDAO(conn);
            PerformedSetDAO setDAO = new PerformedSetDAO(conn);
            new ClientLogicController(ctx, sheetDAO, sessionDAO, setDAO).execute();
        });
    }

    /**
     * Punto di ingresso operativo dell'applicazione.
     * -
     * Gestisce il flusso sequenziale di alto livello:
     * <ol>
     *   <li><b>Boot:</b> Caricamento configurazioni e argomenti CLI.</li>
     *   <li><b>Login Loop:</b> Ciclo infinito di autenticazione fino alla chiusura dell'app.</li>
     *   <li><b>Dispatching:</b> Avvio del modulo operativo corrispondente al ruolo.</li>
     * </ol>
     * 
     * @param args Parametri passati da riga di comando.
     */
    public void run(String[] args) {
        try {
            // FASE 1: BOOT & CONFIGURATION
            BootLogicController bootController = new BootLogicController(sharedScanner);
            Configuration config = bootController.execute(args);

            if (config.uiMode() == UIMode.GUI) {
                view.displayError("Interfaccia Grafica prevista ma non implementata.");
                return;
            }

            boolean exitApp = false;
            while (!exitApp) {
                // FASE 2: HANDSHAKE (Connessione come utente 'login')
                Connection loginConn = DBConnectionManager.getInstance().connectAs(Role.LOGIN);

                ControllerContext loginCtx = new ControllerContext(config, sharedScanner, null, loginConn);
                LoginLogicController loginController = new LoginLogicController(loginCtx);
                AuthToken sessionToken = loginController.execute();

                if (sessionToken == null) {
                    exitApp = true; // Chiusura esplicita richiesta dall'utente
                } else {
                    // FASE 3: ROLE SWITCHING & DISPATCHING
                    // Ricollega il DB con i privilegi operativi del ruolo autenticato
                    initializeDispatchMap(DBConnectionManager.getInstance().getConnection());
                    dispatch(config, sessionToken);
                }
            }

        } catch (Exception e) {
            view.displayError("SISTEMA: Errore fatale non gestito -> " + e.getMessage());
        } finally {
            shutDown();
        }
    }

    /**
     * Reindirizza l'esecuzione al modulo corrispondente al ruolo contenuto nel token.
     */
    private void dispatch(Configuration config, AuthToken token) {
        ModuleLauncher launcher = dispatchMap.get(token.role());
        if (launcher != null) {
            launcher.launch(config, token);
        } else {
            view.displayError("Sicurezza: Ruolo non riconosciuto nel sistema di dispatching.");
        }
    }

    /**
     * Esegue il teardown in sicurezza dell'applicazione.
     * Garantisce la chiusura dei socket database e del buffer di input di sistema.
     */
    private void shutDown() {
        view.displayLine("Chiusura delle risorse e disconnessione dal server...");
        DBConnectionManager.getInstance().closeConnection();
        if (sharedScanner != null) sharedScanner.close();
    }
}
