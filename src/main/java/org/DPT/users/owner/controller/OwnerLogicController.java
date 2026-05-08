package org.DPT.users.owner.controller;

import org.DPT.boot.model.Configuration;
import org.DPT.exception.DatabaseException;
import org.DPT.shared.catalog.esercizi.dto.ExerciseCreationDTO;
import org.DPT.shared.catalog.macchinari.dto.MachineCreationDTO;
import org.DPT.shared.catalog.esercizi.dao.ExerciseDAO;
import org.DPT.shared.catalog.macchinari.dao.MachineDAO;
import org.DPT.users.client.dao.ClientDAO;
import org.DPT.users.common.dto.ClientCreationDTO;
import org.DPT.users.common.dto.UserCreationDTO;
import org.DPT.users.common.utils.UserTypeHandlerI;
import org.DPT.users.login.model.AuthToken;
import org.DPT.users.owner.dao.OwnerDAO;
import org.DPT.users.owner.factory.OwnerUIFactory;
import org.DPT.users.owner.model.Owner;
import org.DPT.users.pt.dao.PTDAO;
import org.DPT.users.receptionist.dao.ReceptionistDAO;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class OwnerLogicController {

    private final OwnerUI ui;
    private final AuthToken token;
    private final Owner profile;

    private final OwnerDAO ownerDAO = new OwnerDAO(); // istanziato internamente
    private final PTDAO ptDAO;
    private final ReceptionistDAO receptionistDAO;
    private final ClientDAO clientDAO;
    private final MachineDAO machineDAO;
    private final ExerciseDAO exerciseDAO;

    private final Map<String, UserTypeHandlerI> userHandlers = new HashMap<>();

    public OwnerLogicController(Configuration config, Scanner scanner, AuthToken token,
                                PTDAO ptDAO, ReceptionistDAO receptionistDAO, ClientDAO clientDAO,
                                MachineDAO machineDAO, ExerciseDAO exerciseDAO) {
        this.ui = OwnerUIFactory.getUI(config.uiMode(), scanner);
        this.token = token;
        this.ptDAO = ptDAO;
        this.receptionistDAO = receptionistDAO;
        this.clientDAO = clientDAO;
        this.machineDAO = machineDAO;
        this.exerciseDAO = exerciseDAO;

        this.profile = ownerDAO.findById(token.userId())
                .orElseThrow(() -> new DatabaseException("Profilo proprietario non trovato."));

        initializeHandlers();
    }

    private void initializeHandlers() {
        // Gestore per i Personal Trainer
        userHandlers.put("PT", new UserTypeHandlerI() {
            @Override public void showList() { ui.showUtenti(ptDAO.getAll(), "PT"); }
            @Override public void toggleStatus() { ptDAO.updateStatus(ui.askForIDUtente(), ui.askForNewStatus()); }
            @Override public void createNew() { ptDAO.insert(ui.askForStaffData()); }
        });

        // Gestore per gli Addetti Segreteria
        userHandlers.put("ADDETTO SEGRETERIA", new UserTypeHandlerI() {
            @Override public void showList() { ui.showUtenti(receptionistDAO.getAll(), "ADDETTI SEGRETERIA"); }
            @Override public void toggleStatus() { receptionistDAO.updateStatus(ui.askForIDUtente(), ui.askForNewStatus()); }
            @Override public void createNew() { receptionistDAO.insert(ui.askForStaffData()); }
        });

        // Gestore per i Clienti
        userHandlers.put("CLIENTE", new UserTypeHandlerI() {
            @Override public void showList() { ui.showUtenti(clientDAO.getAll(), "CLIENTI"); }
            @Override public void toggleStatus() {
                int id = ui.askForIDUtente();
                if (ui.askForNewStatus()) clientDAO.activate(id);
                else clientDAO.deactivate(id);
            }
            @Override public void createNew() { ui.reportError("I Clienti possono essere inseriti solo dalla Segreteria."); }
        });
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
                case 3 -> manageUtenze();
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

    private void manageUtenze() {
        boolean back = false;
        while (!back) {
            ui.showUtenzeMenu();
            int choice = ui.askForChoice();
            switch (choice) {
                case 1 -> manageUtenzaSpecifica("PT");
                case 2 -> manageUtenzaSpecifica("ADDETTO SEGRETERIA");
                case 3 -> manageUtenzaSpecifica("CLIENTE");
                case 0 -> back = true;
                default -> ui.reportError("Scelta non valida.");
            }
        }
    }

    private void manageUtenzaSpecifica(String tipo) {
        UserTypeHandlerI handler = userHandlers.get(tipo);
        if (handler == null) return;

        boolean back = false;
        while (!back) {
            ui.showUtenzaActionMenu(tipo);
            int choice = ui.askForChoice();
            try {
                switch (choice) {
                    case 1 -> handler.showList();
                    case 2 -> handler.toggleStatus();
                    case 3 -> handler.createNew();
                    case 0 -> back = true;
                    default -> ui.reportError("Scelta non valida.");
                }
            } catch (Exception e) {
                ui.reportError(e.getMessage());
            }
        }
    }
}
