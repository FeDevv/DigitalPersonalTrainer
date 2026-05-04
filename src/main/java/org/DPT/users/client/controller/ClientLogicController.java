package org.DPT.users.client.controller;

import org.DPT.boot.model.Configuration;
import org.DPT.exception.DatabaseException;
import org.DPT.shared.workout.session.dao.WorkoutSessionDAO;
import org.DPT.shared.workout.session.model.WorkoutSession;
import org.DPT.shared.workout.set.dao.PerformedSetDAO;
import org.DPT.shared.workout.sheet.dao.WorkoutSheetDAO;
import org.DPT.shared.workout.sheet.model.ActiveSheetItem;
import org.DPT.shared.workout.sheet.model.WorkoutSheet;
import org.DPT.users.client.dao.ClientDAO;
import org.DPT.users.client.factory.ClientUIFactory;
import org.DPT.users.client.model.Client;
import org.DPT.users.login.model.AuthToken;

import java.util.List;
import java.util.Scanner;

/**
 * Controller Logico per il modulo Cliente.
 * Gestisce l'esecuzione dell'allenamento e la consultazione dei dati personali.
 */
public class ClientLogicController {

    private final ClientUI ui;
    private final AuthToken token;
    private final Client profile;

    private final ClientDAO clientDAO;
    private final WorkoutSheetDAO sheetDAO;
    private final WorkoutSessionDAO sessionDAO;
    private final PerformedSetDAO setDAO;

    public ClientLogicController(Configuration config, Scanner scanner, AuthToken token,
                                 ClientDAO clientDAO, WorkoutSheetDAO sheetDAO,
                                 WorkoutSessionDAO sessionDAO, PerformedSetDAO setDAO) {
        this.ui = ClientUIFactory.getUI(config.uiMode(), scanner);
        this.token = token;
        this.clientDAO = clientDAO;
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
            // 1. Verifica presenza scheda attiva
            List<ActiveSheetItem> routine = sheetDAO.getActiveRoutine(profile.getId());
            if (routine.isEmpty()) {
                ui.reportError("Non hai una scheda attiva. Contatta il tuo Personal Trainer!");
                return;
            }

            String sheetName = routine.get(0).sheetName();
            int sheetId = routine.get(0).sheetId();

            // 2. Avvio Sessione (Il DB popola automaticamente le serie)
            WorkoutSession session = sessionDAO.startSession(sheetId);
            ui.showWorkoutStart(sheetName);

            int totalCompleted = 0;
            int totalExpected = 0;
            for (ActiveSheetItem item : routine) {
                totalExpected += item.expectedSets();
            }

            boolean workoutInterrupted = false;

            // 3. Training Loop (Esercizi)
            for (int i = 0; i < routine.size() && !workoutInterrupted; i++) {
                ActiveSheetItem exercise = routine.get(i);
                ui.showExerciseProgress(i + 1, routine.size(), exercise.exerciseName(), exercise.executionNotes());

                // 4. Sub-loop (Serie)
                for (int s = 1; s <= exercise.expectedSets(); s++) {
                    ui.showSetProgress(s, exercise.expectedSets(), exercise.expectedReps(), exercise.restTime());
                    
                    int action = ui.askSetAction();
                    
                    if (action == 1) { // FATTO
                        Double weight = exercise.bodyweight() ? null : ui.askForWeight();
                        setDAO.updatePerformance(session.id(), exercise.exerciseId(), s, weight, true);
                        totalCompleted++;
                        ui.reportSuccess("Serie registrata!");
                        
                        // Timer di recupero (non mostrato dopo l'ultima serie dell'ultimo esercizio)
                        if (!(i == routine.size() - 1 && s == exercise.expectedSets())) {
                            ui.showRestTimer(exercise.restTime());
                        }
                    } else if (action == 2) { // SALTA SERIE
                        ui.reportInfo("Serie saltata.");
                    } else if (action == 3) { // SALTA ESERCIZIO
                        ui.reportInfo("Esercizio saltato.");
                        break; // Esce dal loop delle serie
                    } else if (action == 0) { // TERMINA ALLENAMENTO
                        workoutInterrupted = true;
                        break;
                    }
                }
            }

            // 5. Chiusura Sessione
            sessionDAO.endSession(session.id());
            
            // Recupero dati aggiornati per il riepilogo (la percentuale è calcolata dai trigger)
            List<WorkoutSession> updatedSessions = sessionDAO.findAllBySheetId(sheetId);
            int finalPercentage = updatedSessions.stream()
                    .filter(s -> s.id() == session.id())
                    .findFirst()
                    .map(WorkoutSession::completionPercentage)
                    .orElse(0);

            ui.showWorkoutSummary(totalCompleted, totalExpected, finalPercentage);

        } catch (Exception e) {
            ui.reportError("Errore durante l'allenamento: " + e.getMessage());
        }
    }

    private void viewActiveRoutine() {
        try {
            List<ActiveSheetItem> routine = sheetDAO.getActiveRoutine(profile.getId());
            ui.showActiveRoutine(routine);
        } catch (DatabaseException e) {
            ui.reportError(e.getMessage());
        }
    }

    private void viewHistory() {
        try {
            List<WorkoutSheet> history = sheetDAO.findAllByClientId(profile.getId());
            ui.showSheetHistory(history);
        } catch (DatabaseException e) {
            ui.reportError(e.getMessage());
        }
    }
}
