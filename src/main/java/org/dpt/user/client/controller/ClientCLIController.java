package org.dpt.user.client.controller;

import org.dpt.shared.mvc.AbstractCLIController;
import org.dpt.domain.workout.sheet.model.ActiveSheetItem;
import org.dpt.domain.workout.sheet.model.WorkoutSheet;
import org.dpt.user.client.view.ClientCLIView;

import java.util.List;
import java.util.Scanner;

/**
 * Implementazione CLI dell'interfaccia utente per il modulo Cliente.
 * -
 * Gestisce l'interazione testuale per l'esecuzione degli allenamenti, includendo 
 * la visualizzazione del progresso, la gestione dei timer di recupero e 
 * l'acquisizione dei dati di performance (serie, ripetizioni, carichi).
 */
public class ClientCLIController extends AbstractCLIController implements ClientUI {

    private final ClientCLIView clientView;

    /**
     * Inizializza il controller iniettando lo scanner e la vista dedicata.
     */
    public ClientCLIController(Scanner scanner) {
        super(scanner, new ClientCLIView());
        this.clientView = (ClientCLIView) super.view;
    }

    @Override
    public void showHeader(String clientName) { clientView.displayClientHeader(clientName); }

    @Override
    public void showMainMenu() { clientView.displayMainMenu(); }

    @Override
    public int askForChoice() { return readInt(""); }

    @Override
    public void showRoutine(String title, List<ActiveSheetItem> routine) { clientView.displayActiveRoutine(title, routine); }

    @Override
    public void showSheetHistory(List<WorkoutSheet> history) { clientView.displaySheetHistory(history); }

    @Override
    public int askForID(String prompt) {
        return readInt(prompt);
    }

    // --- WORKOUT MANAGEMENT ---

    /** Segnala l'inizio di una nuova sessione di allenamento. */
    public void showWorkoutStart(String sheetName) { clientView.displayWorkoutStart(sheetName); }

    /** Mostra l'intestazione dell'esercizio corrente con eventuali note tecniche del PT. */
    @Override
    public void showExerciseProgress(int currentEx, int totalEx, String exName, String notes) {
        clientView.displayExerciseHeader(currentEx, totalEx, exName, notes);
    }

    /** Visualizza il progresso delle serie per l'esercizio in corso. */
    @Override
    public void showSetProgress(int currentSet, int totalSets, int reps) {
        clientView.displaySetInfo(currentSet, totalSets, reps);
    }

    /** 
     * Richiede l'esito della serie. Gestisce la logica di navigazione 
     * (prossima serie, salto esercizio, chiusura anticipata).
     */
    @Override
    public int askSetAction() {
        clientView.displaySetMenu();
        int choice = readInt("");
        while (choice < 0 || choice > 3) {
            clientView.displayError("Scelta non valida.");
            choice = readInt("");
        }
        return choice;
    }

    /**
     * Acquisisce il carico utilizzato per la serie corrente. 
     * Supporta l'input opzionale per mantenere i valori di default/precedenti.
     */
    @Override
    public Double askForWeight() {
        String input = readOptionalString("Carico utilizzato (kg) [premi invio per saltare]:");
        if (input.isEmpty()) return null;
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException _) {
            clientView.displayError("Formato non valido. Peso impostato a NULL.");
            return null;
        }
    }

    /** 
     * Simula un timer di recupero testuale. 
     * L'utente deve confermare manualmente la fine del recupero per procedere.
     */
    @Override
    public void showRestTimer(int seconds) {
        clientView.displayRestTimer(seconds);
        scanner.nextLine(); 
    }

    /** Visualizza il riepilogo statistico al termine della sessione. */
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
