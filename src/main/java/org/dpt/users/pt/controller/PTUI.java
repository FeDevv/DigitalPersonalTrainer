package org.dpt.users.pt.controller;

import org.dpt.shared.catalog.esercizi.model.Exercise;
import org.dpt.shared.catalog.macchinari.model.Machine;
import org.dpt.shared.workout.sheet.model.ActiveSheetItem;
import org.dpt.shared.workout.sheet.model.SheetItem;
import org.dpt.shared.workout.sheet.model.WorkoutSheet;
import org.dpt.users.client.model.Client;
import org.dpt.users.pt.model.PerformanceDTO;

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
    void showSheetDetails(String title, List<ActiveSheetItem> details);
    void showCatalog(List<Machine> machines, List<Exercise> exercises);
    int askForID(String prompt);

    // Reporting
    LocalDate askForStartDate();
    LocalDate askForEndDate();
    void showPerformanceReport(List<PerformanceDTO> report);

    void reportError(String message);
    void reportSuccess(String message);
    void reportGoodbye();

}
