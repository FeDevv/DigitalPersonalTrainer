package org.dpt.user.owner.controller;

import org.dpt.exception.DatabaseException;
import org.dpt.shared.mvc.ControllerContext;
import org.dpt.domain.catalog.exercise.dto.ExerciseCreationDTO;
import org.dpt.domain.catalog.machine.dto.MachineCreationDTO;
import org.dpt.domain.catalog.exercise.dao.ExerciseDAO;
import org.dpt.domain.catalog.machine.dao.MachineDAO;
import org.dpt.user.client.dao.ClientDAO;
import org.dpt.user.management.controller.UserManagementController;
import org.dpt.shared.mvc.AbstractLogicController;
import org.dpt.user.owner.dao.OwnerDAO;
import org.dpt.user.owner.factory.OwnerUIFactory;
import org.dpt.user.owner.model.Owner;
import org.dpt.user.pt.dao.PTDAO;
import org.dpt.user.receptionist.dao.ReceptionistDAO;

public class OwnerLogicController extends AbstractLogicController {

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
            case 1 -> manageMachines();
            case 2 -> manageExercises();
            case 3 -> userManagementController.manageUsers();
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

    private void manageMachines() {
        boolean back = false;
        while (!back) {
            ui.showMachineMenu();
            int choice = ui.askForChoice();
            try {
                switch (choice) {
                    case 1 -> ui.showMachines(machineDAO.getAll());
                    case 2 -> {
                        int id = ui.askForMachineIDToggle();
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

    private void manageExercises() {
        boolean back = false;
        while (!back) {
            ui.showExerciseMenu();
            int choice = ui.askForChoice();
            try {
                switch (choice) {
                    case 1 -> ui.showExercises(exerciseDAO.getAll());
                    case 2 -> {
                        int id = ui.askForExerciseIDToggle();
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
