package org.dpt.users.receptionist.controller;

import org.dpt.exception.DatabaseException;
import org.dpt.shared.context.ControllerContext;
import org.dpt.users.client.dao.ClientDAO;
import org.dpt.users.common.controller.UserManagementController;
import org.dpt.users.pt.dao.PTDAO;
import org.dpt.users.receptionist.dao.AssignmentDAO;
import org.dpt.users.receptionist.dao.ReceptionistDAO;
import org.dpt.users.receptionist.factory.ReceptionistUIFactory;
import org.dpt.users.receptionist.model.Receptionist;

/**
 * Controller Logico per il modulo Addetto Segreteria.
 */
public class ReceptionistLogicController {

    private final ReceptionistUI ui;
    private final Receptionist profile;

    // DAO Locali
    private final ReceptionistDAO receptionistDAO;
    private final AssignmentDAO assignmentDAO;

    // DAO Esterni
    private final PTDAO ptDAO;
    private final ClientDAO clientDAO;

    private final UserManagementController userManagementController;

    public ReceptionistLogicController(ControllerContext ctx, PTDAO ptDAO, ClientDAO clientDAO) {
        this.ui = ReceptionistUIFactory.getUI(ctx.config().uiMode(), ctx.scanner());
        this.ptDAO = ptDAO;
        this.clientDAO = clientDAO;

        this.receptionistDAO = new ReceptionistDAO(ctx.connection());
        this.assignmentDAO = new AssignmentDAO(ctx.connection());

        this.profile = receptionistDAO.findById(ctx.token().userId())
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
