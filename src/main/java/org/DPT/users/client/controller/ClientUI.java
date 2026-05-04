package org.DPT.users.client.controller;

import org.DPT.shared.workout.sheet.model.ActiveSheetItem;
import org.DPT.shared.workout.sheet.model.WorkoutSheet;

import java.util.List;

/**
 * Contratto per l'interfaccia utente del modulo Cliente (Atleta).
 */
public interface ClientUI {
    void showHeader(String clientName);
    void showMainMenu();
    int askForChoice();

    // Visualizzazione Schede
    void showActiveSheetHeader(String sheetName);
    void showActiveRoutine(List<ActiveSheetItem> routine);
    void showSheetHistory(List<WorkoutSheet> history);

    // Flusso Allenamento
    void showWorkoutStart(String sheetName);
    void showExerciseProgress(int currentEx, int totalEx, String exName, String notes);
    void showSetProgress(int currentSet, int totalSets, int reps, int restTime);
    
    /**
     * Chiede l'esito della serie corrente.
     * @return 1 per Completata, 2 per Salta Serie, 3 per Salta Esercizio, 0 per Termina Allenamento.
     */
    int askSetAction();
    
    /**
     * Chiede il carico utilizzato per la serie (se non è a corpo libero).
     * @return Il peso inserito o null se non specificato.
     */
    Double askForWeight();

    void showRestTimer(int seconds);
    void showWorkoutSummary(int completedSets, int totalSets, int percentage);

    void reportError(String message);
    void reportSuccess(String message);
    void reportInfo(String message);
    void reportGoodbye();
}
