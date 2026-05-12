package org.dpt.users.owner.controller;

import org.dpt.exception.DatabaseException;
import org.dpt.shared.context.ControllerContext;
import org.dpt.shared.catalog.exercises.dto.ExerciseCreationDTO;
import org.dpt.shared.catalog.machinery.dto.MachineCreationDTO;
import org.dpt.shared.catalog.exercises.dao.ExerciseDAO;
import org.dpt.shared.catalog.machinery.dao.MachineDAO;
import org.dpt.users.client.dao.ClientDAO;
import org.dpt.users.common.controller.UserManagementController;
import org.dpt.shared.ui.BaseLogicController;
import org.dpt.users.owner.dao.OwnerDAO;
import org.dpt.users.owner.factory.OwnerUIFactory;
import org.dpt.users.owner.model.Owner;
import org.dpt.users.pt.dao.PTDAO;
import org.dpt.users.receptionist.dao.ReceptionistDAO;

public class OwnerLogicController extends BaseLogicController {

    private final OwnerUI ui;
    private final Owner profile;
    private final MachineDAO machineDAO;
    private final ExerciseDAO exerciseDAO;
    private final UserManagementController userManagementController;

    private static final String INVALID_CHOICE = "scelta non valida.";

    public OwnerLogicController(ControllerContext ctx, 
                                PTDAO ptDAO, ReceptionistDAO receptionistDAO, ClientDAO clientDAO,
                                MachineDAO machineDAO, ExerciseDAO exerciseDAO) {
        super(ctx);
        this.ui = OwnerUIFactory.getUI(ctx.config().uiMode(), ctx.scanner());
        this.machineDAO = machineDAO;
        this.exerciseDAO = exerciseDAO;

        OwnerDAO ownerDAO = new OwnerDAO(ctx.connection());

        this.profile = ownerDAO.findById(ctx.token().userId())
                .orElseThrow(() -> new DatabaseException("Profilo proprietario non trovato."));

        this.userManagementController = new UserManagementController(ui, ptDAO, receptionistDAO, clientDAO, false);
    }

    @Override
    protected boolean isUserActive() {
        return true; // Il proprietario non è disattivabile
    }

    @Override
    protected void showHeader() {
        ui.showHeader(profile.getFirstName());
    }

    @Override
    protected void renderMenu() {
        ui.showMainMenu();
    }

    @Override
    protected int askForChoice() {
        return ui.askForChoice();
    }

    @Override
    protected void handleChoice(int choice) {
        switch (choice) {
            case 1 -> manageMacchinari();
            case 2 -> manageEsercizi();
            case 3 -> userManagementController.manageUtenze();
            default -> ui.reportError(INVALID_CHOICE);
        }
    }

    @Override
    protected void onLogout() {
        ui.reportGoodbye();
    }

    @Override
    protected void reportError(String message) {
        ui.reportError(message);
    }

    private void manageMacchinari() {
        boolean back = false;
        while (!back) {
            ui.showMacchinariMenu();
            int choice = ui.askForChoice();
            try {
                switch (choice) {
                    case 1 -> ui.showMacchinari(machineDAO.getAll());
                    case 2 -> {
                        int id = ui.askForIDMacchinarioDaToggle();
                        boolean status = ui.askForNewStatus();
                        machineDAO.updateStatus(id, status);
                        ui.reportSuccess("Stato aggiornato.");
                    }
                    case 3 -> {
                        MachineCreationDTO data = ui.askForMachineData();
                        machineDAO.insert(data, profile.getId());
                        ui.reportSuccess("Macchinario inserito.");
                    }
                    case 0 -> back = true;
                    default -> ui.reportError(INVALID_CHOICE);
                }
            } catch (DatabaseException e) {
                ui.reportError(e.getMessage());
            }
        }
    }

    private void manageEsercizi() {
        boolean back = false;
        while (!back) {
            ui.showEserciziMenu();
            int choice = ui.askForChoice();
            try {
                switch (choice) {
                    case 1 -> ui.showEsercizi(exerciseDAO.getAll());
                    case 2 -> {
                        int id = ui.askForIDEsercizioDaToggle();
                        boolean status = ui.askForNewStatus();
                        exerciseDAO.updateStatus(id, status);
                        ui.reportSuccess("Stato aggiornato.");
                    }
                    case 3 -> {
                        ExerciseCreationDTO data = ui.askForExerciseData(machineDAO.findAll(true));
                        exerciseDAO.insert(data, profile.getId());
                        ui.reportSuccess("Esercizio inserito.");
                    }
                    case 0 -> back = true;
                    default -> ui.reportError(INVALID_CHOICE);
                }
            } catch (DatabaseException e) {
                ui.reportError(e.getMessage());
            }
        }
    }
}
