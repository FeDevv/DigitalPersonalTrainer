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

/**
 * Controller logico principale per le funzionalità del Personal Trainer.
 * -
 * Implementa il nucleo della programmazione sportiva del sistema. Orchestra:
 * <ul>
 *   <li><b>Redazione Schede:</b> Workflow assistito per la creazione di piani d'allenamento
 *       personalizzati per gli atleti assegnati.</li>
 *   <li><b>Monitoring Prestazioni:</b> Analisi dei progressi e del volume di allenamento 
 *       tramite reportistica temporale.</li>
 *   <li><b>Consultazione Tecnica:</b> Accesso al catalogo degli esercizi e delle macchine
 *       per supportare la scelta degli stimoli allenanti.</li>
 * </ul>
 */
public class PTLogicController extends AbstractLogicController {

    private final PTUI ui;
    private final PT profile;
    private final PTDAO ptDAO;
    private final ClientDAO clientDAO;
    private final WorkoutSheetDAO sheetDAO;
    private final MachineDAO machineDAO;
    private final ExerciseDAO exerciseDAO;

    /**
     * Inizializza il modulo PT caricandone il profilo e iniettando i DAO di dominio necessari.
     */
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
                .orElseThrow(() -> new DatabaseException("Profilo Personal Trainer non trovato."));
    }

    /**
     * Heartbeat: Verifica dinamica dello stato di attività del PT.
     * @return true se l'istruttore è ancora abilitato ad operare nel centro.
     */
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

    /** Dispatcher delle funzionalità PT. */
    @Override
    protected void handleChoice(int choice) {
        switch (choice) {
            case 1 -> createNewWorkoutSheet();
            case 2 -> viewSheetHistory();
            case 3 -> generateReport();
            case 4 -> viewCatalog();
            default -> ui.reportError("Selezione non valida.");
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
     * Avvia la procedura di creazione di una nuova scheda di allenamento.
     * Coordina la selezione del cliente, l'invocazione della Stored Procedure di testata
     * e l'inserimento ciclico degli esercizi con i parametri tecnici.
     */
    private void createNewWorkoutSheet() {
        try {
            // Filtro autorizzativo: opera solo sui clienti assegnati
            List<Client> assignedClients = clientDAO.findAssignedToPT(profile.getId());
            if (assignedClients.isEmpty()) {
                ui.reportError("Non risultano atleti associati al tuo profilo attualmente.");
                return;
            }

            int clientId = ui.askForClientId(assignedClients);
            String title = ui.askForSheetTitle();

            // Transazione implicita via DB: crea la scheda e disattiva la precedente
            sheetDAO.createNewSheet(profile.getId(), clientId, title, 0);

            WorkoutSheet newSheet = sheetDAO.findActiveByClientId(clientId)
                    .orElseThrow(() -> new DatabaseException("La scheda è stata creata ma il sistema non riesce a recuperarne il riferimento attivo."));

            // Loop di popolamento dettaglio esercizi
            boolean adding = true;
            while (adding) {
                int exerciseId = ui.askForExerciseId(exerciseDAO.findAllSelectable());
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

            ui.reportSuccess("La scheda '" + title + "' è stata attivata con successo.");
        } catch (Exception e) {
            ui.reportError("Errore durante la redazione del piano: " + e.getMessage());
        }
    }

    /** Visualizza lo storico delle programmazioni effettuate dal PT corrente. */
    private void viewSheetHistory() {
        try {
            List<WorkoutSheet> history = sheetDAO.findByPTId(profile.getId());
            ui.showSheetHistory(history);
            
            if (!history.isEmpty()) {
                int sheetId = ui.askForID("Inserisci l'ID della Scheda per visualizzare il dettaglio esercizi (0 per uscire):");
                if (sheetId != 0) {
                    history.stream()
                            .filter(s -> s.id() == sheetId)
                            .findFirst()
                            .ifPresentOrElse(
                                    s -> ui.showSheetDetails(s.title(), sheetDAO.getSheetDetails(sheetId)),
                                    () -> ui.reportError("L'ID inserito non fa parte del tuo storico professionale.")
                            );
                }
            }
        } catch (DatabaseException e) {
            ui.reportError(e.getMessage());
        }
    }

    /** Genera statistiche sull'andamento degli allenamenti degli atleti. */
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

    /** Consultazione rapida dei macchinari e degli esercizi attivi. */
    private void viewCatalog() {
        try {
            ui.showCatalog(machineDAO.findAll(true), exerciseDAO.findAll(true));
        } catch (DatabaseException e) {
            ui.reportError(e.getMessage());
        }
    }
}
