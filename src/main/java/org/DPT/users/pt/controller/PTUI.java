package org.DPT.users.pt.controller;

import org.DPT.shared.catalog.esercizi.model.Exercise;
import org.DPT.shared.catalog.macchinari.model.Machine;
import org.DPT.shared.workout.sheet.model.SheetItem;
import org.DPT.shared.workout.sheet.model.WorkoutSheet;
import org.DPT.users.client.model.Client;

import java.time.LocalDate;
import java.util.List;

/**
 * Contratto per l'interfaccia utente del modulo Personal Trainer.
 */
public interface PTUI {
    void showHeader(String ptName);
    void showMainMenu();
    
    int askForChoice();

    // Gestione Schede
    void showAssignedClients(List<Client> clients);
    int askForClientId(List<Client> availableClients);
    String askForSheetTitle();
    
    // Composizione Scheda
    void showExerciseCatalog(List<Exercise> exercises);
    int askForExerciseId(List<Exercise> availableExercises);
    SheetItem askForExerciseDetails(int sheetId, int exerciseId);
    boolean askIfAddAnotherExercise();

    // Visualizzazione
    void showSheetHistory(List<WorkoutSheet> sheets);
    void showCatalog(List<Machine> machines, List<Exercise> exercises);

    // Reporting
    LocalDate askForStartDate();
    LocalDate askForEndDate();
    void showPerformanceReport(List<PerformanceRecord> report);

    void reportError(String message);
    void reportSuccess(String message);
    void reportGoodbye();

    // Record di supporto per il report (interno all'interfaccia o in un file separato)
    record PerformanceRecord(
            String clientName,
            LocalDate date,
            int duration,
            int completionPercentage
    ) {}
}
