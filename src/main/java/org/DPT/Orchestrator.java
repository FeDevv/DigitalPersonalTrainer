package org.DPT;

import org.DPT.boot.controller.BootLogicController;
import org.DPT.boot.model.Configuration;
import org.DPT.boot.model.UIMode;
import org.DPT.connection.DBConnectionManager;
import org.DPT.shared.auth.Role;
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
import org.DPT.users.receptionist.dao.ReceptionistDAO;

import java.sql.Connection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Orchestrator centrale dell'applicazione Digital Personal Trainer.
 */
public class Orchestrator {

    private final Scanner sharedScanner;

    @FunctionalInterface
    private interface ModuleLauncher {
        void launch(Configuration config, AuthToken token);
    }

    private final Map<Role, ModuleLauncher> dispatchMap = new EnumMap<>(Role.class);

    public Orchestrator() {
        this.sharedScanner = new Scanner(System.in);
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
            new ClientLogicController(config, sharedScanner, token, conn,
                    clientDAO, sheetDAO, sessionDAO, setDAO).execute();
        });
    }

    public void run(String[] args) {
        try {
            BootLogicController bootController = new BootLogicController(sharedScanner);
            Configuration config = bootController.execute(args);

            if (config.uiMode() == UIMode.GUI) {
                System.out.println("\n[AVVISO] Interfaccia Grafica non ancora implementata.");
                return;
            }

            Connection loginConn = DBConnectionManager.getInstance().connectAs(Role.LOGIN);
            LoginLogicController loginController = new LoginLogicController(config, sharedScanner, loginConn);
            AuthToken sessionToken = loginController.execute();

            if (sessionToken == null) return;

            initializeDispatchMap(DBConnectionManager.getInstance().getConnection());
            dispatch(config, sessionToken);

        } catch (Exception e) {
            System.err.println("\n[ERRORE DI SISTEMA] " + e.getMessage());
        } finally {
            shutDown();
        }
    }

    private void dispatch(Configuration config, AuthToken token) {
        ModuleLauncher launcher = dispatchMap.get(token.role());
        if (launcher != null) {
            launcher.launch(config, token);
        } else {
            System.err.println("\n[ERRORE] Ruolo non riconosciuto.");
        }
    }

    private void shutDown() {
        System.out.println("\nChiusura applicazione...");
        DBConnectionManager.getInstance().closeConnection();
        if (sharedScanner != null) sharedScanner.close();
    }
}
