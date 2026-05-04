package org.DPT.users.client.controller;

import org.DPT.shared.ui.BaseCLIController;
import org.DPT.shared.workout.sheet.model.ActiveSheetItem;
import org.DPT.shared.workout.sheet.model.WorkoutSheet;
import org.DPT.users.client.view.ClientCLIView;

import java.util.List;
import java.util.Scanner;

public class ClientCLIController extends BaseCLIController implements ClientUI {

    private final ClientCLIView clientView;

    public ClientCLIController(Scanner scanner) {
        super(scanner, new ClientCLIView());
        this.clientView = (ClientCLIView) super.view;
    }

    @Override
    public void showHeader(String clientName) { clientView.displayClientHeader(clientName); }

    @Override
    public void showMainMenu() { clientView.displayMainMenu(); }

    @Override
    public int askForChoice() { return readInt("\n>> "); }

    @Override
    public void showActiveSheetHeader(String sheetName) { clientView.displaySectionTitle("Scheda Attiva: " + sheetName); }

    @Override
    public void showActiveRoutine(List<ActiveSheetItem> routine) { clientView.displayActiveRoutine(routine); }

    @Override
    public void showSheetHistory(List<WorkoutSheet> history) { clientView.displaySheetHistory(history); }

    @Override
    public void showWorkoutStart(String sheetName) { clientView.displayWorkoutStart(sheetName); }

    @Override
    public void showExerciseProgress(int currentEx, int totalEx, String exName, String notes) {
        clientView.displayExerciseHeader(currentEx, totalEx, exName, notes);
    }

    @Override
    public void showSetProgress(int currentSet, int totalSets, int reps, int restTime) {
        clientView.displaySetInfo(currentSet, totalSets, reps, restTime);
    }

    @Override
    public int askSetAction() {
        clientView.displaySetMenu();
        int choice = readInt(">> ");
        while (choice < 0 || choice > 3) {
            clientView.displayError("Scelta non valida.");
            choice = readInt(">> ");
        }
        return choice;
    }

    @Override
    public Double askForWeight() {
        String input = readString("Carico utilizzato (kg) [premi invio per saltare]");
        if (input.isEmpty()) return null;
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            clientView.displayError("Formato non valido. Peso impostato a NULL.");
            return null;
        }
    }

    @Override
    public void showRestTimer(int seconds) {
        clientView.displayRestTimer(seconds);
        scanner.nextLine(); // Attende il tasto Invio per continuare
    }

    @Override
    public void showWorkoutSummary(int completedSets, int totalSets, int percentage) {
        clientView.displayWorkoutSummary(completedSets, totalSets, percentage);
    }

    @Override
    public void reportInfo(String message) { clientView.displayLine("\n[INFO] " + message); }

    @Override
    public void reportSuccess(String message) { clientView.displaySuccess(message); }

    @Override
    public void reportGoodbye() { clientView.displayGoodbye(); }
}
