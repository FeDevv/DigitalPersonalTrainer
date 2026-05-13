package org.dpt.user.pt.controller;

import org.dpt.exception.DatabaseException;
import org.dpt.shared.mvc.ControllerContext;
import org.dpt.domain.catalog.exercise.dao.ExerciseDAO;
import org.dpt.domain.catalog.machine.dao.MachineDAO;
import org.dpt.domain.workout.sheet.dao.WorkoutSheetDAO;
import org.dpt.domain.workout.sheet.model.SheetItem;
import org.dpt.domain.workout.sheet.model.WorkoutSheet;
import org.dpt.user.client.dao.ClientDAO;
import org.dpt.user.client.model.Client;
import org.dpt.user.pt.dao.PTDAO;
import org.dpt.shared.mvc.AbstractLogicController;
import org.dpt.user.pt.factory.PTUIFactory;
import org.dpt.user.pt.model.PT;
import org.dpt.user.pt.model.PerformanceDTO;

import java.time.LocalDate;
import java.util.List;

public class PTLogicController extends AbstractLogicController {

    private final PTUI ui;
    private final PT profile;
    private final PTDAO ptDAO;
    private final ClientDAO clientDAO;
    private final WorkoutSheetDAO sheetDAO;
    private final MachineDAO machineDAO;
    private final ExerciseDAO exerciseDAO;

    public PTLogicController(ControllerContext ctx,
                             ClientDAO clientDAO, WorkoutSheetDAO sheetDAO,
                             MachineDAO machineDAO, ExerciseDAO exerciseDAO) {
        super(ctx);
        this.ui = PTUIFactory.getUI(ctx.config().uiMode(), ctx.scanner());
        this.clientDAO = clientDAO;
        this.sheetDAO = sheetDAO;
        this.machineDAO = machineDAO;
        this.exerciseDAO = exerciseDAO;

        this.ptDAO = new PTDAO(ctx.connection());

        this.profile = ptDAO.findById(ctx.token().userId())
                .orElseThrow(() -> new DatabaseException("Profilo PT non trovato."));
    }

    @Override
    protected boolean isUserActive() {
        return ptDAO.findById(profile.getId())
                .map(PT::isActive)
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
            case 1 -> createNewWorkoutSheet();
            case 2 -> viewSheetHistory();
            case 3 -> generateReport();
            case 4 -> viewCatalog();
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

    private void createNewWorkoutSheet() {
        try {
            List<Client> assignedClients = clientDAO.findAssignedToPT(profile.getId());
            if (assignedClients.isEmpty()) {
                ui.reportError("Non hai clienti assegnati attualmente.");
                return;
            }

            int clientId = ui.askForClientId(assignedClients);
            String title = ui.askForSheetTitle();

            sheetDAO.createNewSheet(profile.getId(), clientId, title, 0);

            WorkoutSheet newSheet = sheetDAO.findActiveByClientId(clientId)
                    .orElseThrow(() -> new DatabaseException("Errore critico: scheda creata ma non trovata."));

            boolean adding = true;
            while (adding) {
                int exerciseId = ui.askForExerciseId(exerciseDAO.findAll(true));
                SheetItem details = ui.askForExerciseDetails(newSheet.id(), exerciseId);
                
                sheetDAO.addExerciseToSheet(
                        details.sheetId(),
                        details.exerciseId(),
                        details.restTime(),
                        details.executionNotes(),
                        details.expectedSets(),
                        details.expectedReps()
                );
                
                adding = ui.askIfAddAnotherExercise();
            }

            ui.reportSuccess("Scheda '" + title + "' creata e attivata con successo.");
        } catch (Exception e) {
            ui.reportError("Errore durante la creazione della scheda: " + e.getMessage());
        }
    }

    private void viewSheetHistory() {
        try {
            List<WorkoutSheet> history = sheetDAO.findByPTId(profile.getId());
            ui.showSheetHistory(history);
            
            if (!history.isEmpty()) {
                int sheetId = ui.askForID("Inserisci ID Scheda per i dettagli (0 per uscire):");
                if (sheetId != 0) {
                    history.stream()
                            .filter(s -> s.id() == sheetId)
                            .findFirst()
                            .ifPresentOrElse(
                                    s -> ui.showSheetDetails(s.title(), sheetDAO.getSheetDetails(sheetId)),
                                    () -> ui.reportError("ID non trovato nel tuo storico.")
                            );
                }
            }
        } catch (DatabaseException e) {
            ui.reportError(e.getMessage());
        }
    }

    private void generateReport() {
        try {
            LocalDate start = ui.askForStartDate();
            LocalDate end = ui.askForEndDate();
            List<PerformanceDTO> report = ptDAO.getPerformanceReport(profile.getId(), start, end);
            ui.showPerformanceReport(report);
        } catch (DatabaseException e) {
            ui.reportError(e.getMessage());
        }
    }

    private void viewCatalog() {
        try {
            ui.showCatalog(machineDAO.getAll(), exerciseDAO.getAll());
        } catch (DatabaseException e) {
            ui.reportError(e.getMessage());
        }
    }
}
