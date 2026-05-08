package org.DPT.users.receptionist.controller;

import org.DPT.boot.model.Configuration;
import org.DPT.exception.DatabaseException;
import org.DPT.users.client.dao.ClientDAO;
import org.DPT.users.common.controller.UserManagementController;
import org.DPT.users.login.model.AuthToken;
import org.DPT.users.pt.dao.PTDAO;
import org.DPT.users.receptionist.dao.AssignmentDAO;
import org.DPT.users.receptionist.dao.ReceptionistDAO;
import org.DPT.users.receptionist.factory.ReceptionistUIFactory;
import org.DPT.users.receptionist.model.Receptionist;

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

    private final UserManagementController userManagementController;

    public ReceptionistLogicController(Configuration config, Scanner scanner, AuthToken token,
                                       PTDAO ptDAO, ClientDAO clientDAO) {
        this.ui = ReceptionistUIFactory.getUI(config.uiMode(), scanner);
        this.token = token;
        this.ptDAO = ptDAO;
        this.clientDAO = clientDAO;

        this.profile = receptionistDAO.findById(token.userId())
                .orElseThrow(() -> new DatabaseException("Profilo addetto non trovato."));

        this.userManagementController = new UserManagementController(ui, ptDAO, receptionistDAO, clientDAO, true);
    }

    public void execute() {
        ui.showHeader(profile.getFirstName());
        boolean logout = false;

        while (!logout) {
            ui.showMainMenu();
            int choice = ui.askForChoice();

            switch (choice) {
                case 1 -> userManagementController.manageUtenze();
                case 2 -> makeAssignment();
                case 0 -> logout = true;
                default -> ui.reportError("Scelta non valida.");
            }
        }
        ui.reportGoodbye();
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
