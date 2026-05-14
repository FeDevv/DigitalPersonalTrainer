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
 * Controller logico principale per le funzionalità dell'Addetto Segreteria.
 * -
 * Orchestra le operazioni amministrative e di front-office del sistema, tra cui:
 * <ul>
 *   <li><b>Gestione Utenze:</b> Delega al {@link UserManagementController} la manutenzione
 *       delle anagrafiche di tutto il personale e dei clienti.</li>
 *   <li><b>Intermediazione Contrattuale:</b> Gestisce il workflow di assegnazione tra 
 *       Personal Trainer e Clienti tramite {@link AssignmentDAO}.</li>
 *   <li><b>Integrità della Sessione:</b> Verifica costantemente lo stato dell'utenza 
 *       corrente per prevenire accessi da account disattivati (soft-delete).</li>
 * </ul>
 */
public class ReceptionistLogicController extends AbstractLogicController {

    private final ReceptionistUI ui;
    private final Receptionist profile;

    private final AssignmentDAO assignmentDAO;

    /** Controller riutilizzabile per le operazioni CRUD sugli utenti. */
    private final UserManagementController userManagementController;

    /**
     * Inizializza il modulo Segreteria iniettando i DAO necessari per le operazioni cross-entità.
     */
    public ReceptionistLogicController(ControllerContext ctx, PTDAO ptDAO, ClientDAO clientDAO) {
        super(ctx);
        this.ui = ReceptionistUIFactory.getUI(ctx.config().uiMode(), ctx.scanner());

        ReceptionistDAO receptionistDAO = new ReceptionistDAO(ctx.connection());
        this.assignmentDAO = new AssignmentDAO(ctx.connection());

        this.profile = receptionistDAO.findById(ctx.token().userId())
                .orElseThrow(() -> new DatabaseException("Profilo addetto non trovato."));

        this.userManagementController = new UserManagementController(ui, ptDAO, receptionistDAO, clientDAO, true);
    }

    /**
     * Verifica se l'operatore è ancora attivo nel database.
     * Implementa un controllo "Fail-Fast" per bloccare sessioni di utenti disattivati.
     */
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

    /**
     * Gestisce il dispatching delle funzionalità del modulo.
     * 1. Gestione Utenze: Apre il sottomenu condiviso.
     * 2. Assegnazione: Avvia il workflow di legame PT-CLIENTE.
     */
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

    /**
     * Coordina il workflow di assegnazione.
     * Richiede l'input IDs all'interfaccia e invoca la logica transazionale sul DB tramite DAO.
     */
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
