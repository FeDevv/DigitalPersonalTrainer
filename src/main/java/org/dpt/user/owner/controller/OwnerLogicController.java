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

/**
 * Controller logico principale per le funzionalità del Proprietario.
 * -
 * Estende {@link AbstractLogicController} implementando il workflow operativo 
 * dedicato alla gestione totale del centro sportivo.
 * -
 * Il controller orchestra tre macro-aree funzionali:
 * <ul>
 *   <li><b>Gestione Macchinari:</b> Coordinamento della manutenzione del parco macchine.</li>
 *   <li><b>Gestione Catalogo (Esercizi):</b> Definizione del "saper fare" tecnico della palestra.</li>
 *   <li><b>Gestione Risorse Umane:</b> Amministrazione dello staff e dei clienti tramite 
 *       delega al {@link UserManagementController}.</li>
 * </ul>
 */
public class OwnerLogicController extends AbstractLogicController {

    private final OwnerUI ui;
    private final Owner profile;
    private final MachineDAO machineDAO;
    private final ExerciseDAO exerciseDAO;
    
    /** Controller iniettato per la gestione condivisa delle anagrafiche. */
    private final UserManagementController userManagementController;

    private static final String INVALID_CHOICE = "Selezione non valida.";

    /**
     * Inizializza il modulo Owner caricando il profilo dell'utente loggato.
     * @param ctx Contesto di esecuzione.
     * @param ptDAO DAO per i Personal Trainer (per delega management).
     * @param receptionistDAO DAO per la segreteria (per delega management).
     * @param clientDAO DAO per i clienti (per delega management).
     * @param machineDAO DAO per la gestione macchinari.
     * @param exerciseDAO DAO per la gestione esercizi.
     */
    public OwnerLogicController(ControllerContext ctx, 
                                PTDAO ptDAO, ReceptionistDAO receptionistDAO, ClientDAO clientDAO,
                                MachineDAO machineDAO, ExerciseDAO exerciseDAO) {
        super(ctx);
        this.ui = OwnerUIFactory.getUI(ctx.config().uiMode(), ctx.scanner());
        this.machineDAO = machineDAO;
        this.exerciseDAO = exerciseDAO;

        OwnerDAO ownerDAO = new OwnerDAO(ctx.connection());

        this.profile = ownerDAO.findById(ctx.token().userId())
                .orElseThrow(() -> new DatabaseException("Profilo proprietario non trovato nel sistema."));

        // Inizializza il gestore utenti disabilitando la creazione diretta dei clienti per l'Owner (policy di dominio)
        this.userManagementController = new UserManagementController(ui, ptDAO, receptionistDAO, clientDAO, false);
    }

    @Override
    protected boolean isUserActive() {
        return true; // Il profilo Proprietario non è soggetto a disattivazione software via soft-delete
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
     * Dispatcher delle funzionalità Owner.
     */
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

    /** Workflow per la gestione dell'anagrafica macchinari. */
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
                        ui.reportSuccess("Stato del macchinario aggiornato.");
                    }
                    case 3 -> {
                        MachineCreationDTO data = ui.askForMachineData();
                        machineDAO.insert(data, profile.getId());
                        ui.reportSuccess("Nuovo macchinario registrato.");
                    }
                    case 0 -> back = true;
                    default -> ui.reportError(INVALID_CHOICE);
                }
            } catch (DatabaseException e) {
                ui.reportError(e.getMessage());
            }
        }
    }

    /** Workflow per la gestione del catalogo esercizi. */
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
                        ui.reportSuccess("stato dell'esercizio aggiornato.");
                    }
                    case 3 -> {
                        // Passa solo i macchinari attivi per l'associazione
                        ExerciseCreationDTO data = ui.askForExerciseData(machineDAO.findAll(true));
                        exerciseDAO.insert(data, profile.getId());
                        ui.reportSuccess("Nuovo esercizio aggiunto.");
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
