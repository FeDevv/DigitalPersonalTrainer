package org.dpt;

import org.dpt.boot.controller.BootLogicController;
import org.dpt.boot.model.Configuration;
import org.dpt.boot.model.UIMode;
import org.dpt.connection.DBConnectionManager;
import org.dpt.shared.auth.Role;
import org.dpt.shared.catalog.exercises.dao.ExerciseDAO;
import org.dpt.shared.catalog.machinery.dao.MachineDAO;
import org.dpt.shared.context.ControllerContext;
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
    private final BaseCLIView view;

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
            ClientDAO clientDAO = new ClientDAO(conn);
            WorkoutSheetDAO sheetDAO = new WorkoutSheetDAO(conn);
            WorkoutSessionDAO sessionDAO = new WorkoutSessionDAO(conn);
            PerformedSetDAO setDAO = new PerformedSetDAO(conn);
            new ClientLogicController(ctx, clientDAO, sheetDAO, sessionDAO, setDAO).execute();
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
            
            // FASE 2: AUTHENTICATION
            // Creiamo un contesto temporaneo senza token per il login
            ControllerContext loginCtx = new ControllerContext(config, sharedScanner, null, loginConn);
            LoginLogicController loginController = new LoginLogicController(loginCtx);
            AuthToken sessionToken = loginController.execute();

            if (sessionToken == null) return;

            // FASE 3: DISPATCHING
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
        if (sharedScanner != null) sharedScanner.close();
    }
}
