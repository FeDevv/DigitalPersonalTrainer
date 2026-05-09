package org.DPT.users.owner.controller;

import org.DPT.boot.model.Configuration;
import org.DPT.exception.DatabaseException;
import org.DPT.shared.auth.Role;
import org.DPT.shared.catalog.esercizi.dto.ExerciseCreationDTO;
import org.DPT.shared.catalog.macchinari.dto.MachineCreationDTO;
import org.DPT.shared.catalog.esercizi.dao.ExerciseDAO;
import org.DPT.shared.catalog.macchinari.dao.MachineDAO;
import org.DPT.users.client.dao.ClientDAO;
import org.DPT.users.common.controller.UserManagementController;
import org.DPT.users.login.model.AuthToken;
import org.DPT.users.owner.dao.OwnerDAO;
import org.DPT.users.owner.factory.OwnerUIFactory;
import org.DPT.users.owner.model.Owner;
import org.DPT.users.pt.dao.PTDAO;
import org.DPT.users.receptionist.dao.ReceptionistDAO;

import java.sql.Connection;
import java.util.Scanner;

public class OwnerLogicController {

    private final OwnerUI ui;
    private final AuthToken token;
    private final Owner profile;

    private final OwnerDAO ownerDAO;
    private final PTDAO ptDAO;
    private final ReceptionistDAO receptionistDAO;
    private final ClientDAO clientDAO;
    private final MachineDAO machineDAO;
    private final ExerciseDAO exerciseDAO;

    private final UserManagementController userManagementController;

    public OwnerLogicController(Configuration config, Scanner scanner, AuthToken token, Connection conn,
                                PTDAO ptDAO, ReceptionistDAO receptionistDAO, ClientDAO clientDAO,
                                MachineDAO machineDAO, ExerciseDAO exerciseDAO) {
        this.ui = OwnerUIFactory.getUI(config.uiMode(), scanner);
        this.token = token;
        this.ptDAO = ptDAO;
        this.receptionistDAO = receptionistDAO;
        this.clientDAO = clientDAO;
        this.machineDAO = machineDAO;
        this.exerciseDAO = exerciseDAO;

        this.ownerDAO = new OwnerDAO(conn);

        this.profile = ownerDAO.findById(token.userId())
                .orElseThrow(() -> new DatabaseException("Profilo proprietario non trovato."));

        this.userManagementController = new UserManagementController(ui, ptDAO, receptionistDAO, clientDAO, false);
    }

    public void execute() {
        ui.showHeader(profile.getFirstName());
        boolean logout = false;

        while (!logout) {
            ui.showMainMenu();
            int choice = ui.askForChoice();

            switch (choice) {
                case 1 -> manageMacchinari();
                case 2 -> manageEsercizi();
                case 3 -> userManagementController.manageUtenze();
                case 0 -> logout = true;
                default -> ui.reportError("Scelta non valida.");
            }
        }
        ui.reportGoodbye();
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
                    default -> ui.reportError("Scelta non valida.");
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
                    default -> ui.reportError("Scelta non valida.");
                }
            } catch (DatabaseException e) {
                ui.reportError(e.getMessage());
            }
        }
    }
}
