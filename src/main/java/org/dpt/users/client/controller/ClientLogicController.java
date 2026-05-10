package org.dpt.users.client.controller;

import org.dpt.boot.model.Configuration;
import org.dpt.exception.DatabaseException;
import org.dpt.shared.workout.session.dao.WorkoutSessionDAO;
import org.dpt.shared.workout.session.model.WorkoutSession;
import org.dpt.shared.workout.set.dao.PerformedSetDAO;
import org.dpt.shared.workout.sheet.dao.WorkoutSheetDAO;
import org.dpt.shared.workout.sheet.model.ActiveSheetItem;
import org.dpt.shared.workout.sheet.model.WorkoutSheet;
import org.dpt.users.client.dao.ClientDAO;
import org.dpt.users.client.factory.ClientUIFactory;
import org.dpt.users.client.model.Client;
import org.dpt.users.login.model.AuthToken;

import java.util.List;
import java.util.Scanner;

/**
 * Controller Logico per il modulo Cliente.
 */
public class ClientLogicController {

    private final ClientUI ui;
    private final Client profile;
    private final WorkoutSheetDAO sheetDAO;
    private final WorkoutSessionDAO sessionDAO;
    private final PerformedSetDAO setDAO;

    // Stato temporaneo per la sessione corrente
    private boolean workoutInterrupted;
    private int totalCompleted;

    public ClientLogicController(Configuration config, Scanner scanner, AuthToken token,
                                 ClientDAO clientDAO, WorkoutSheetDAO sheetDAO,
                                 WorkoutSessionDAO sessionDAO, PerformedSetDAO setDAO) {
        this.ui = ClientUIFactory.getUI(config.uiMode(), scanner);
        this.sheetDAO = sheetDAO;
        this.sessionDAO = sessionDAO;
        this.setDAO = setDAO;

        this.profile = clientDAO.findById(token.userId())
                .orElseThrow(() -> new DatabaseException("Profilo cliente non trovato."));
    }

    public void execute() {
        ui.showHeader(profile.getFirstName());
        boolean logout = false;

        while (!logout) {
            ui.showMainMenu();
            int choice = ui.askForChoice();

            switch (choice) {
                case 1 -> startWorkoutSession();
                case 2 -> viewActiveRoutine();
                case 3 -> viewHistory();
                case 0 -> logout = true;
                default -> ui.reportError("Scelta non valida.");
            }
        }
        ui.reportGoodbye();
    }

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

            // Reset stato sessione
            this.workoutInterrupted = false;
            this.totalCompleted = 0;

            executeRoutine(routine, session.id());

            finalizeSession(session.id(), sheetId, routine);

        } catch (Exception e) {
            ui.reportError("Errore durante l'allenamento: " + e.getMessage());
        }
    }

    private void executeRoutine(List<ActiveSheetItem> routine, int sessionId) {
        for (int i = 0; i < routine.size() && !workoutInterrupted; i++) {
            ActiveSheetItem exercise = routine.get(i);
            ui.showExerciseProgress(i + 1, routine.size(), exercise.exerciseName(), exercise.executionNotes());
            executeExercise(exercise, sessionId, i == routine.size() - 1);
        }
    }

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

    private void handleSetDone(ActiveSheetItem exercise, int sessionId, int setNumber) {
        Double weight = exercise.bodyweight() ? null : ui.askForWeight();
        setDAO.updatePerformance(sessionId, exercise.exerciseId(), setNumber, weight, true);
        this.totalCompleted++;
        ui.reportSuccess("Serie registrata!");
    }

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

    private void viewActiveRoutine() {
        try {
            List<ActiveSheetItem> routine = sheetDAO.getActiveRoutine(profile.getId());
            String title = routine.isEmpty() ? "Tua Routine Corrente" : "Tua Routine Corrente: " + routine.get(0).sheetName();
            ui.showRoutine(title, routine);
        } catch (DatabaseException e) {
            ui.reportError(e.getMessage());
        }
    }

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
