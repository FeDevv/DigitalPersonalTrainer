package org.dpt.user.receptionist.controller;

import org.dpt.exception.DatabaseException;
import org.dpt.shared.mvc.ControllerContext;
import org.dpt.user.client.dao.ClientDAO;
import org.dpt.user.management.controller.UserManagementController;
import org.dpt.user.pt.dao.PTDAO;
import org.dpt.shared.mvc.AbstractLogicController;
import org.dpt.user.receptionist.dao.AssignmentDAO;
import org.dpt.user.receptionist.dao.ReceptionistDAO;
import org.dpt.user.receptionist.factory.ReceptionistUIFactory;
import org.dpt.user.receptionist.model.Receptionist;

/**
 * Controller Logico per il modulo Addetto Segreteria.
 */
public class ReceptionistLogicController extends AbstractLogicController {

    private final ReceptionistUI ui;
    private final Receptionist profile;

    private final AssignmentDAO assignmentDAO;

    private final UserManagementController userManagementController;

    public ReceptionistLogicController(ControllerContext ctx, PTDAO ptDAO, ClientDAO clientDAO) {
        super(ctx);
        this.ui = ReceptionistUIFactory.getUI(ctx.config().uiMode(), ctx.scanner());

        ReceptionistDAO receptionistDAO = new ReceptionistDAO(ctx.connection());
        this.assignmentDAO = new AssignmentDAO(ctx.connection());

        this.profile = receptionistDAO.findById(ctx.token().userId())
                .orElseThrow(() -> new DatabaseException("Profilo addetto non trovato."));

        this.userManagementController = new UserManagementController(ui, ptDAO, receptionistDAO, clientDAO, true);
    }

    @Override
    protected boolean isUserActive() {
        return new ReceptionistDAO(context.connection()).findById(profile.getId())
                .map(Receptionist::isActive)
                .orElse(false);
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
            case 1 -> userManagementController.manageUsers();
            case 2 -> makeAssignment();
            default -> ui.reportError("Scelta non valida.");
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
