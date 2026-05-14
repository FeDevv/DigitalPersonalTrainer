package org.dpt.user.client.controller;

import org.dpt.exception.DatabaseException;
import org.dpt.shared.mvc.ControllerContext;
import org.dpt.shared.mvc.AbstractLogicController;
import org.dpt.domain.workout.session.dao.WorkoutSessionDAO;
import org.dpt.domain.workout.session.model.WorkoutSession;
import org.dpt.domain.workout.set.dao.PerformedSetDAO;
import org.dpt.domain.workout.sheet.dao.WorkoutSheetDAO;
import org.dpt.domain.workout.sheet.model.ActiveSheetItem;
import org.dpt.domain.workout.sheet.model.WorkoutSheet;
import org.dpt.user.client.dao.ClientDAO;
import org.dpt.user.client.factory.ClientUIFactory;
import org.dpt.user.client.model.Client;

import java.util.List;

/**
 * Controller logico principale per le funzionalità dell'Area Cliente.
 * -
 * Gestisce l'interazione del cliente con il sistema, orchestrando:
 * <ul>
 *   <li><b>Tracking Allenamento:</b> Gestisce il ciclo di vita di una sessione di 
 *       allenamento, registrando in tempo reale le serie completate e i carichi.</li>
 *   <li><b>Analisi Storica:</b> Permette la consultazione delle schede passate e dei 
 *       relativi dettagli tecnici.</li>
 *   <li><b>Sincronizzazione DB:</b> Coordina l'aggiornamento delle tabelle {@code SESSIONE} 
 *       e {@code SERIE_ESEGUITA}, garantendo la persistenza del progresso.</li>
 * </ul>
 */
public class ClientLogicController extends AbstractLogicController {

    private final ClientUI ui;
    private final Client profile;
    private final ClientDAO clientDAO;
    private final WorkoutSheetDAO sheetDAO;
    private final WorkoutSessionDAO sessionDAO;
    private final PerformedSetDAO setDAO;

    /** Contatore di sessione per le serie effettivamente completate. */
    private int totalCompleted;
    /** Flag di controllo per l'interruzione anticipata del workflow di allenamento. */
    private boolean workoutInterrupted;

    /**
     * Inizializza il modulo Cliente iniettando i DAO necessari per la gestione 
     * completa dell'esperienza di allenamento.
     */
    public ClientLogicController(ControllerContext ctx,
                                 WorkoutSheetDAO sheetDAO,
                                 WorkoutSessionDAO sessionDAO, PerformedSetDAO setDAO) {
        super(ctx);
        this.ui = ClientUIFactory.getUI(ctx.config().uiMode(), ctx.scanner());
        this.clientDAO = new ClientDAO(ctx.connection());
        this.sheetDAO = sheetDAO;
        this.sessionDAO = sessionDAO;
        this.setDAO = setDAO;

        this.profile = this.clientDAO.findById(ctx.token().userId())
                .orElseThrow(() -> new DatabaseException("Profilo cliente non trovato."));
    }

    /**
     * Verifica se il cliente è ancora attivo (abilitato alla palestra).
     */
    @Override
    protected boolean isUserActive() {
        return clientDAO.findById(profile.getId())
                .map(Client::isActive)
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
            case 1 -> startWorkoutSession();
            case 2 -> viewActiveRoutine();
            case 3 -> viewHistory();
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
     * Workflow principale di avvio allenamento.
     * 1. Recupera la routine attiva tramite {@link WorkoutSheetDAO}.
     * 2. Inizializza una nuova sessione sul DB.
     * 3. Avvia il loop di esecuzione esercizi.
     */
    private void startWorkoutSession() {
        try {
            List<ActiveSheetItem> routine = sheetDAO.getActiveRoutine(profile.getId());
            if (routine.isEmpty()) {
                ui.reportError("Non hai una scheda attiva. Contatta il tuo Personal Trainer!");
                return;
            }

            int sheetId = routine.get(0).sheetId();
            WorkoutSession session = sessionDAO.startSession(sheetId);
            ui.showWorkoutStart(routine.get(0).sheetName());

            this.workoutInterrupted = false;
            this.totalCompleted = 0;

            executeRoutine(routine, session.id());

            finalizeSession(session.id(), sheetId, routine);

        } catch (Exception e) {
            ui.reportError("Errore durante l'allenamento: " + e.getMessage());
        }
    }

    /**
     * Cicla sugli esercizi previsti nella scheda.
     */
    private void executeRoutine(List<ActiveSheetItem> routine, int sessionId) {
        for (int i = 0; i < routine.size() && !workoutInterrupted; i++) {
            ActiveSheetItem exercise = routine.get(i);
            ui.showExerciseProgress(i + 1, routine.size(), exercise.exerciseName(), exercise.executionNotes());
            executeExercise(exercise, sessionId, i == routine.size() - 1);
        }
    }

    /**
     * Gestisce l'esecuzione delle serie per un singolo esercizio.
     * Implementa la logica di skipping e interruzione globale.
     */
    private void executeExercise(ActiveSheetItem exercise, int sessionId, boolean isLastExercise) {
        boolean skipExercise = false;
        int expectedSets = exercise.expectedSets();

        for (int s = 1; s <= expectedSets && !skipExercise && !workoutInterrupted; s++) {
            ui.showSetProgress(s, expectedSets, exercise.expectedReps());
            int action = ui.askSetAction();
            
            switch (action) {
                case 1 -> { // FATTO
                    handleSetDone(exercise, sessionId, s);
                    if (!(isLastExercise && s == expectedSets)) {
                        ui.showRestTimer(exercise.restTime());
                    }
                }
                case 2 -> ui.reportInfo("Serie saltata.");
                case 3 -> {
                    ui.reportInfo("Esercizio saltato.");
                    skipExercise = true;
                }
                case 0 -> workoutInterrupted = true;
                default -> ui.reportError("Azione non valida.");
            }
        }
    }

    /**
     * Registra il completamento di una serie, gestendo l'eventuale input del carico.
     */
    private void handleSetDone(ActiveSheetItem exercise, int sessionId, int setNumber) {
        Double weight = exercise.bodyweight() ? null : ui.askForWeight();
        setDAO.updatePerformance(sessionId, exercise.exerciseId(), setNumber, weight, true);
        this.totalCompleted++;
        ui.reportSuccess("Serie registrata!");
    }

    /**
     * Chiude la sessione e mostra il riepilogo finale con la percentuale di completamento
     * calcolata dal DB (tramite i trigger associati alla tabella SESSIONE).
     */
    private void finalizeSession(int sessionId, int sheetId, List<ActiveSheetItem> routine) {
        sessionDAO.endSession(sessionId);
        
        int totalExpected = routine.stream()
                .mapToInt(ActiveSheetItem::expectedSets)
                .sum();

        int finalPercentage = sessionDAO.findAllBySheetId(sheetId).stream()
                .filter(s -> s.id() == sessionId)
                .findFirst()
                .map(WorkoutSession::completionPercentage)
                .orElse(0);

        ui.showWorkoutSummary(totalCompleted, totalExpected, finalPercentage);
    }

    /** Mostra i dettagli tecnici della routine attualmente assegnata al cliente. */
    private void viewActiveRoutine() {
        try {
            List<ActiveSheetItem> routine = sheetDAO.getActiveRoutine(profile.getId());
            String title = routine.isEmpty() ? "Tua Routine Corrente" : "Tua Routine Corrente: " + routine.get(0).sheetName();
            ui.showRoutine(title, routine);
        } catch (DatabaseException e) {
            ui.reportError(e.getMessage());
        }
    }

    /** Mostra lo storico di tutte le schede d'allenamento (attive e archiviate). */
    private void viewHistory() {
        try {
            List<WorkoutSheet> history = sheetDAO.findAllByClientId(profile.getId());
            ui.showSheetHistory(history);
            
            if (!history.isEmpty()) {
                handleHistorySelection(history);
            }
        } catch (DatabaseException e) {
            ui.reportError(e.getMessage());
        }
    }

    /** Permette di esplorare i dettagli di una scheda specifica dallo storico. */
    private void handleHistorySelection(List<WorkoutSheet> history) {
        int sheetId = ui.askForID("Inserisci ID Scheda per i dettagli (0 per uscire):");
        if (sheetId != 0) {
            history.stream()
                    .filter(s -> s.id() == sheetId)
                    .findFirst()
                    .ifPresentOrElse(
                            s -> ui.showRoutine("Dettaglio Scheda: " + s.title(), sheetDAO.getSheetDetails(sheetId)),
                            () -> ui.reportError("ID non trovato nel tuo storico.")
                    );
        }
    }
}
