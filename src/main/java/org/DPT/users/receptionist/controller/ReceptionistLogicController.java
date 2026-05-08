package org.DPT.users.receptionist.controller;

import org.DPT.boot.model.Configuration;
import org.DPT.exception.DatabaseException;
import org.DPT.users.client.dao.ClientDAO;
import org.DPT.users.common.dto.ClientCreationDTO;
import org.DPT.users.common.dto.UserCreationDTO;
import org.DPT.users.common.utils.UserTypeHandlerI;
import org.DPT.users.login.model.AuthToken;
import org.DPT.users.pt.dao.PTDAO;
import org.DPT.users.receptionist.dao.AssignmentDAO;
import org.DPT.users.receptionist.dao.ReceptionistDAO;
import org.DPT.users.receptionist.factory.ReceptionistUIFactory;
import org.DPT.users.receptionist.model.Receptionist;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Controller Logico per il modulo Addetto Segreteria.
 * Gestisce l'anagrafica utenti e le assegnazioni PT-Cliente.
 */
public class ReceptionistLogicController {

    private final ReceptionistUI ui;
    private final AuthToken token;
    private final Receptionist profile;

    // DAO Locali (Istanziati internamente)
    private final ReceptionistDAO receptionistDAO = new ReceptionistDAO();
    private final AssignmentDAO assignmentDAO = new AssignmentDAO();

    // DAO Esterni (Ricevuti tramite DI)
    private final PTDAO ptDAO;
    private final ClientDAO clientDAO;

    private final Map<String, UserTypeHandlerI> userHandlers = new HashMap<>();

    public ReceptionistLogicController(Configuration config, Scanner scanner, AuthToken token,
                                       PTDAO ptDAO, ClientDAO clientDAO) {
        this.ui = ReceptionistUIFactory.getUI(config.uiMode(), scanner);
        this.token = token;
        this.ptDAO = ptDAO;
        this.clientDAO = clientDAO;

        this.profile = receptionistDAO.findById(token.userId())
                .orElseThrow(() -> new DatabaseException("Profilo addetto non trovato."));

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
        userHandlers.put("ADDETTO", new UserTypeHandlerI() {
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
            @Override public void createNew() { clientDAO.insert(ui.askForClientData()); }
        });
    }

    public void execute() {
        ui.showHeader(profile.getFirstName());
        boolean logout = false;

        while (!logout) {
            ui.showMainMenu();
            int choice = ui.askForChoice();

            switch (choice) {
                case 1 -> manageUtenze();
                case 2 -> makeAssignment();
                case 0 -> logout = true;
                default -> ui.reportError("Scelta non valida.");
            }
        }
        ui.reportGoodbye();
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

    private void makeAssignment() {
        try {
            int ptId = ui.askForPTId();
            int clientId = ui.askForClientId();
            
            assignmentDAO.createAssignment(ptId, clientId, profile.getId());
            ui.reportSuccess("Assegnazione completata con successo.");
        } catch (Exception e) {
            ui.reportError("Impossibile completare l'assegnazione: " + e.getMessage());
        }
    }
}
