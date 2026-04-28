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
import org.DPT.users.login.model.AuthToken;
import org.DPT.users.owner.dao.OwnerDAO;
import org.DPT.users.owner.factory.OwnerUIFactory;
import org.DPT.users.owner.model.Owner;
import org.DPT.users.pt.dao.PTDAO;
import org.DPT.users.receptionist.dao.ReceptionistDAO;

import java.util.Scanner;

public class OwnerLogicController {

    private final OwnerUI ui;
    private final AuthToken token;
    private final Owner profile;

    private final OwnerDAO ownerDAO = new OwnerDAO();
    private final PTDAO ptDAO = new PTDAO();
    private final ReceptionistDAO receptionistDAO = new ReceptionistDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final MachineDAO machineDAO = new MachineDAO();
    private final ExerciseDAO exerciseDAO = new ExerciseDAO();

    public OwnerLogicController(Configuration config, Scanner scanner, AuthToken token) {
        this.ui = OwnerUIFactory.getUI(config.uiMode(), scanner);
        this.token = token;
        this.profile = ownerDAO.findById(token.userId())
                .orElseThrow(() -> new DatabaseException("Profilo proprietario non trovato."));
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
                        MachineCreationDTO data = ui.askForMachineData();
                        machineDAO.insert(data, profile.getId());
                        ui.reportSuccess("Macchinario inserito.");
                    }
                    case 3 -> {
                        int id = ui.askForIDMacchinarioDaToggle();
                        boolean status = ui.askForNewStatus();
                        machineDAO.updateStatus(id, status);
                        ui.reportSuccess("Stato aggiornato.");
                    }
                    case 0 -> back = true;
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
                        ExerciseCreationDTO data = ui.askForExerciseData();
                        exerciseDAO.insert(data, profile.getId());
                        ui.reportSuccess("Esercizio inserito.");
                    }
                    case 3 -> {
                        int id = ui.askForIDEsercizioDaToggle();
                        boolean status = ui.askForNewStatus();
                        exerciseDAO.updateStatus(id, status);
                        ui.reportSuccess("Stato aggiornato.");
                    }
                    case 0 -> back = true;
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
                case 2 -> manageUtenzaSpecifica("ADDETTO");
                case 3 -> manageUtenzaSpecifica("CLIENTE");
                case 0 -> back = true;
            }
        }
    }

    private void manageUtenzaSpecifica(String tipo) {
        boolean back = false;
        while (!back) {
            ui.showUtenzaActionMenu(tipo);
            int choice = ui.askForChoice();
            try {
                switch (choice) {
                    case 1 -> {
                        if (tipo.equals("PT")) ui.showUtenti(ptDAO.getAll(), "PT");
                        else if (tipo.equals("ADDETTO")) ui.showUtenti(receptionistDAO.getAll(), "ADDETTI");
                        else ui.showUtenti(clientDAO.getAll(), "CLIENTI");
                    }
                    case 2 -> {
                        if (tipo.equals("CLIENTE")) {
                            ClientCreationDTO data = ui.askForClientData();
                            clientDAO.insert(data);
                        } else {
                            UserCreationDTO data = ui.askForStaffData();
                            if (tipo.equals("PT")) ptDAO.insert(data);
                            else receptionistDAO.insert(data);
                        }
                        ui.reportSuccess(tipo + " inserito.");
                    }
                    case 3 -> {
                        int id = ui.askForIDUtente();
                        boolean status = ui.askForNewStatus();
                        if (tipo.equals("PT")) ptDAO.updateStatus(id, status);
                        else if (tipo.equals("ADDETTO")) receptionistDAO.updateStatus(id, status);
                        else {
                            if (status) clientDAO.activate(id);
                            else clientDAO.deactivate(id);
                        }
                        ui.reportSuccess("Stato aggiornato.");
                    }
                    case 0 -> back = true;
                }
            } catch (Exception e) {
                ui.reportError(e.getMessage());
            }
        }
    }
}
