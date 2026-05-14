package org.dpt.user.pt.controller;

import org.dpt.domain.catalog.exercise.model.Exercise;
import org.dpt.domain.catalog.machine.model.Machine;
import org.dpt.shared.mvc.AbstractCLIController;
import org.dpt.domain.workout.sheet.model.ActiveSheetItem;
import org.dpt.domain.workout.sheet.model.SheetItem;
import org.dpt.domain.workout.sheet.model.WorkoutSheet;
import org.dpt.user.client.model.Client;
import org.dpt.user.pt.model.PerformanceDTO;
import org.dpt.user.pt.view.PTCLIView;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Implementazione concreta dell'interfaccia PTUI per l'ambiente CLI.
 * -
 * Estende {@link AbstractCLIController} per la gestione dei buffer di input.
 * Gestisce l'acquisizione guidata dei parametri di allenamento (serie, rep, carichi)
 * e il parsing robusto delle date per la generazione della reportistica tecnica.
 */
public class PTCLIController extends AbstractCLIController implements PTUI {

    private final PTCLIView ptView;

    /**
     * Inizializza il controller UI associandogli la view specifica per il PT.
     * @param scanner Scanner condiviso per l'input utente.
     */
    public PTCLIController(Scanner scanner) {
        super(scanner, new PTCLIView());
        this.ptView = (PTCLIView) super.view;
    }

    @Override
    public void showHeader(String ptName) { ptView.displayPTHeader(ptName); }

    @Override
    public void showMainMenu() { ptView.displayMainMenu(); }

    @Override
    public int askForChoice() { return readInt(""); }

    @Override
    public void showAssignedClients(List<Client> clients) { ptView.displayClients(clients); }

    /**
     * Presenta la lista degli atleti e richiede la selezione tramite ID.
     */
    @Override
    public int askForClientId(List<Client> availableClients) {
        showAssignedClients(availableClients);
        return readInt("Inserisci l'identificativo (ID) del cliente:");
    }

    @Override
    public String askForSheetTitle() {
        return readString("Titolo descrittivo del piano (es. 'Forza Mesociclo 1'):");
    }

    @Override
    public void showExerciseCatalog(List<Exercise> exercises) { ptView.displayExerciseCatalog(exercises); }

    /**
     * Guida l'istruttore nella selezione di un esercizio attivo dal catalogo.
     */
    @Override
    public int askForExerciseId(List<Exercise> availableExercises) {
        showExerciseCatalog(availableExercises);
        return readInt("Inserisci l'identificativo (ID) dell'esercizio:");
    }

    /**
     * Acquisisce i dettagli tecnici per un esercizio all'interno del piano.
     */
    @Override
    public SheetItem askForExerciseDetails(int sheetId, int exerciseId) {
        int sets = readInt("Numero di Serie previste:");
        int reps = readInt("Ripetizioni per serie:");
        int rest = readInt("Tempo di recupero (secondi):");
        String notes = readOptionalString("Note tecniche di esecuzione (opzionali):");
        return new SheetItem(sheetId, exerciseId, rest, notes, sets, reps);
    }

    @Override
    public boolean askIfAddAnotherExercise() {
        return readString("Vuoi inserire un ulteriore esercizio nel piano? (s/n):").equalsIgnoreCase("s");
    }

    @Override
    public void showSheetHistory(List<WorkoutSheet> sheets) { ptView.displaySheetHistory(sheets); }

    @Override
    public void showSheetDetails(String title, List<ActiveSheetItem> details) {
        ptView.displaySheetDetails(title, details);
    }

    @Override
    public int askForID(String prompt) {
        return readInt(prompt);
    }

    @Override
    public void showCatalog(List<Machine> machines, List<Exercise> exercises) {
        ptView.displayCatalog(machines, exercises);
    }

    @Override
    public LocalDate askForStartDate() {
        return askForDate("Inizio periodo analisi (AAAA-MM-GG):");
    }

    @Override
    public LocalDate askForEndDate() {
        return askForDate("Fine periodo analisi (AAAA-MM-GG):");
    }

    /**
     * Metodo helper per il parsing robusto di oggetti LocalDate tramite stringhe.
     */
    private LocalDate askForDate(String prompt) {
        while (true) {
            String input = readString(prompt);
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException _) {
                ptView.displayError("Formato data non riconosciuto. Utilizzare lo standard ISO-8601 (AAAA-MM-GG).");
            }
        }
    }

    @Override
    public void showPerformanceReport(List<PerformanceDTO> report) {
        ptView.displayPerformanceReport(report);
    }

    @Override
    public void reportSuccess(String message) { ptView.displaySuccess(message); }

    @Override
    public void reportGoodbye() { ptView.displayGoodbye(); }
}
