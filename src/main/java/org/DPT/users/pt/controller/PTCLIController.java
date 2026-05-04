package org.DPT.users.pt.controller;

import org.DPT.shared.catalog.esercizi.model.Exercise;
import org.DPT.shared.catalog.macchinari.model.Machine;
import org.DPT.shared.ui.BaseCLIController;
import org.DPT.shared.workout.sheet.model.SheetItem;
import org.DPT.shared.workout.sheet.model.WorkoutSheet;
import org.DPT.users.client.model.Client;
import org.DPT.users.pt.view.PTCLIView;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class PTCLIController extends BaseCLIController implements PTUI {

    private final PTCLIView ptView;

    public PTCLIController(Scanner scanner) {
        super(scanner, new PTCLIView());
        this.ptView = (PTCLIView) super.view;
    }

    @Override
    public void showHeader(String ptName) { ptView.displayPTHeader(ptName); }

    @Override
    public void showMainMenu() { ptView.displayMainMenu(); }

    @Override
    public int askForChoice() { return readInt("\n>> "); }

    @Override
    public void showAssignedClients(List<Client> clients) { ptView.displayClients(clients); }

    @Override
    public int askForClientId(List<Client> availableClients) {
        showAssignedClients(availableClients);
        return readInt("Inserisci ID Cliente per cui redigere la scheda");
    }

    @Override
    public String askForSheetTitle() {
        return readString("Titolo della Scheda (es. Forza Invernale)");
    }

    @Override
    public void showExerciseCatalog(List<Exercise> exercises) { ptView.displayExerciseCatalog(exercises); }

    @Override
    public int askForExerciseId(List<Exercise> availableExercises) {
        showExerciseCatalog(availableExercises);
        return readInt("Inserisci ID Esercizio da aggiungere");
    }

    @Override
    public SheetItem askForExerciseDetails(int sheetId, int exerciseId) {
        int sets = readInt("Numero di Serie");
        int reps = readInt("Numero di Ripetizioni");
        int rest = readInt("Recupero (in secondi)");
        String notes = readString("Note di esecuzione (opzionale, premi invio per saltare)");
        return new SheetItem(sheetId, exerciseId, rest, notes, sets, reps);
    }

    @Override
    public boolean askIfAddAnotherExercise() {
        return readString("Vuoi aggiungere un altro esercizio? (s/n)").equalsIgnoreCase("s");
    }

    @Override
    public void showSheetHistory(List<WorkoutSheet> sheets) { ptView.displaySheetHistory(sheets); }

    @Override
    public void showCatalog(List<Machine> machines, List<Exercise> exercises) {
        ptView.displayCatalog(machines, exercises);
    }

    @Override
    public LocalDate askForStartDate() {
        return askForDate("Data Inizio Report (AAAA-MM-GG)");
    }

    @Override
    public LocalDate askForEndDate() {
        return askForDate("Data Fine Report (AAAA-MM-GG)");
    }

    private LocalDate askForDate(String prompt) {
        while (true) {
            String input = readString(prompt);
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                ptView.displayError("Formato data non valido. Usa AAAA-MM-GG.");
            }
        }
    }

    @Override
    public void showPerformanceReport(List<PerformanceRecord> report) {
        ptView.displayPerformanceReport(report);
    }

    @Override
    public void reportSuccess(String message) { ptView.displaySuccess(message); }

    @Override
    public void reportGoodbye() { ptView.displayGoodbye(); }
}
