package org.dpt;

import org.dpt.boot.controller.BootLogicController;
import org.dpt.boot.model.Configuration;
import org.dpt.boot.model.UIMode;
import org.dpt.connection.DBConnectionManager;
import org.dpt.shared.auth.Role;
import org.dpt.shared.catalog.esercizi.dao.ExerciseDAO;
import org.dpt.shared.catalog.macchinari.dao.MachineDAO;
import org.dpt.shared.ui.BaseCLIView;
import org.dpt.shared.workout.session.dao.WorkoutSessionDAO;
import org.dpt.shared.workout.set.dao.PerformedSetDAO;
import org.dpt.shared.workout.sheet.dao.WorkoutSheetDAO;
import org.dpt.users.client.controller.ClientLogicController;
import org.dpt.users.client.dao.ClientDAO;
import org.dpt.users.login.controller.LoginLogicController;
import org.dpt.users.login.model.AuthToken;
import org.dpt.users.owner.controller.OwnerLogicController;
import org.dpt.users.pt.controller.PTLogicController;
import org.dpt.users.pt.dao.PTDAO;
import org.dpt.users.receptionist.controller.ReceptionistLogicController;
import org.dpt.users.receptionist.dao.ReceptionistDAO;

import java.sql.Connection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Orchestrator centrale dell'applicazione Digital Personal Trainer.
 */
public class Orchestrator {

    private final Scanner sharedScanner;
    private final BaseCLIView view; // Delegato per l'output globale

    @FunctionalInterface
    private interface ModuleLauncher {
        void launch(Configuration config, AuthToken token);
    }

    private final Map<Role, ModuleLauncher> dispatchMap = new EnumMap<>(Role.class);

    public Orchestrator() {
        this.sharedScanner = new Scanner(System.in);
        this.view = new BaseCLIView();
    }

    private void initializeDispatchMap(Connection conn) {
        dispatchMap.put(Role.OWNER, (config, token) -> {
            PTDAO ptDAO = new PTDAO(conn);
            ReceptionistDAO receptionistDAO = new ReceptionistDAO(conn);
            ClientDAO clientDAO = new ClientDAO(conn);
            MachineDAO machineDAO = new MachineDAO(conn);
            ExerciseDAO exerciseDAO = new ExerciseDAO(conn);
            new OwnerLogicController(config, sharedScanner, token, conn,
                    ptDAO, receptionistDAO, clientDAO, machineDAO, exerciseDAO).execute();
        });

        dispatchMap.put(Role.PT, (config, token) -> {
            ClientDAO clientDAO = new ClientDAO(conn);
            WorkoutSheetDAO sheetDAO = new WorkoutSheetDAO(conn);
            MachineDAO machineDAO = new MachineDAO(conn);
            ExerciseDAO exerciseDAO = new ExerciseDAO(conn);
            new PTLogicController(config, sharedScanner, token, conn,
                    clientDAO, sheetDAO, machineDAO, exerciseDAO).execute();
        });

        dispatchMap.put(Role.RECEPTIONIST, (config, token) -> {
            PTDAO ptDAO = new PTDAO(conn);
            ClientDAO clientDAO = new ClientDAO(conn);
            new ReceptionistLogicController(config, sharedScanner, token, conn,
                    ptDAO, clientDAO).execute();
        });

        dispatchMap.put(Role.CLIENT, (config, token) -> {
            ClientDAO clientDAO = new ClientDAO(conn);
            WorkoutSheetDAO sheetDAO = new WorkoutSheetDAO(conn);
            WorkoutSessionDAO sessionDAO = new WorkoutSessionDAO(conn);
            PerformedSetDAO setDAO = new PerformedSetDAO(conn);
            new ClientLogicController(config, sharedScanner, token,
                    clientDAO, sheetDAO, sessionDAO, setDAO).execute();
        });
    }

    public void run(String[] args) {
        try {
            BootLogicController bootController = new BootLogicController(sharedScanner);
            Configuration config = bootController.execute(args);

            if (config.uiMode() == UIMode.GUI) {
                view.displayError("Interfaccia Grafica non ancora implementata. Riavviare in modalità CLI.");
                return;
            }

            Connection loginConn = DBConnectionManager.getInstance().connectAs(Role.LOGIN);
            LoginLogicController loginController = new LoginLogicController(config, sharedScanner, loginConn);
            AuthToken sessionToken = loginController.execute();

            if (sessionToken == null) return;

            initializeDispatchMap(DBConnectionManager.getInstance().getConnection());
            dispatch(config, sessionToken);

        } catch (Exception e) {
            view.displayError("SISTEMA: " + e.getMessage());
        } finally {
            shutDown();
        }
    }

    private void dispatch(Configuration config, AuthToken token) {
        ModuleLauncher launcher = dispatchMap.get(token.role());
        if (launcher != null) {
            launcher.launch(config, token);
        } else {
            view.displayError("Ruolo non riconosciuto o non configurato.");
        }
    }

    private void shutDown() {
        view.displayLine("Chiusura applicazione...");
        DBConnectionManager.getInstance().closeConnection();
        sharedScanner.close();
    }
}
