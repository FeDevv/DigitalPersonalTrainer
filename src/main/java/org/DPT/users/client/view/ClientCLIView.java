package org.DPT.users.client.view;

import org.DPT.shared.ui.BaseCLIView;
import org.DPT.shared.workout.sheet.model.ActiveSheetItem;
import org.DPT.shared.workout.sheet.model.WorkoutSheet;

import java.util.ArrayList;
import java.util.List;

public class ClientCLIView extends BaseCLIView {

    public void displayClientHeader(String name) {
        displayHeader("AREA CLIENTE - Benvenuto/a " + name);
    }

    public void displayMainMenu() {
        displaySectionTitle("Dashboard");
        displayLine("1. Inizia ALLENAMENTO (Scheda Attiva)");
        displayLine("2. Visualizza ROUTINE Scheda Attiva");
        displayLine("3. Visualizza STORICO SCHEDE");
        displayLine("0. Logout");
    }

    public void displayActiveRoutine(List<ActiveSheetItem> routine) {
        displaySectionTitle("Tua Routine Corrente");
        
        String[] headers = {"ESERCIZIO", "SERIE", "REPS", "RECUPERO"};
        List<String[]> rows = new ArrayList<>();
        for (ActiveSheetItem item : routine) {
            rows.add(new String[]{
                item.exerciseName(),
                String.valueOf(item.expectedSets()),
                String.valueOf(item.expectedReps()),
                item.restTime() + "s"
            });
        }
        
        renderTable(headers, rows, new int[]{25, 6, 6, 10});
    }

    public void displaySheetHistory(List<WorkoutSheet> history) {
        displaySectionTitle("Storico Tue Schede");
        
        String[] headers = {"TITOLO SCHEDA", "DATA CREAZIONE", "STATO"};
        List<String[]> rows = new ArrayList<>();
        for (WorkoutSheet s : history) {
            rows.add(new String[]{
                s.title(),
                s.creationDate().toString(),
                s.active() ? "ATTIVA" : "ARCHIVIATA"
            });
        }
        
        renderTable(headers, rows, new int[]{25, 20, 12});
    }

    // --- WORKOUT EXPERIENCE ---

    public void displayWorkoutStart(String sheetName) {
        displaySectionTitle("Allenamento Avviato");
        displayLine("Scheda: " + sheetName);
        displayLine("Buon allenamento! Ogni serie ti avvicina al tuo obiettivo.");
    }

    public void displayExerciseHeader(int current, int total, String name, String notes) {
        String title = String.format(" ESERCIZIO %d di %d: %s ", current, total, name.toUpperCase());
        int width = Math.max(title.length(), 50);
        
        displayEmptyLine();
        displayLine(TL + H.repeat(width) + TR);
        displayLine(V + title + " ".repeat(width - title.length()) + V);
        if (notes != null && !notes.isBlank()) {
            String noteLine = " Note PT: " + notes + " ";
            displayLine(V + noteLine + " ".repeat(width - noteLine.length()) + V);
        }
        displayLine(BL + H.repeat(width) + BR);
    }

    public void displaySetInfo(int current, int total, int reps, int rest) {
        String progressBar = renderProgressBar(current, total);
        displayLine(String.format("SERIE %d/%d %s | Obiettivo: %d reps", current, total, progressBar, reps));
    }

    public void displaySetMenu() {
        displaySectionTitle("Azioni");
        displayLine("1. [Fatto] Serie completata");
        displayLine("2. [Salta] Questa serie");
        displayLine("3. [Salta] Intero esercizio");
        displayLine("0. [Termina] Chiudi allenamento");
    }

    public void displayRestTimer(int seconds) {
        displayLine("\n >>> RECUPERO: " + seconds + "s <<<");
        displayLine(" (Premi Invio per la prossima serie)");
    }

    public void displayWorkoutSummary(int completed, int total, int percentage) {
        displayHeader("SESSIONE COMPLETATA");
        displayLine("Riepilogo Attività:");
        
        String[] headers = {"DESCRIZIONE", "VALORE"};
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"Serie previste totali", String.valueOf(total)});
        rows.add(new String[]{"Serie completate", String.valueOf(completed)});
        rows.add(new String[]{"Grado di completamento", percentage + "%"});
        
        renderTable(headers, rows, new int[]{25, 10});
        
        displayLine("Progress Score: " + renderProgressBar(percentage / 10, 10));
        
        displayEmptyLine();
        if (percentage == 100) displayLine("ECCELLENTE! Sessione perfetta.");
        else if (percentage > 70) displayLine("OTTIMO LAVORO! Continua con questa costanza.");
        else displayLine("BRAVO! Ogni sessione conta per il tuo progresso.");
    }

    private String renderProgressBar(int current, int total) {
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < total; i++) {
            if (i < current) bar.append("█");
            else bar.append("░");
        }
        bar.append("]");
        return bar.toString();
    }

    public void displayGoodbye() {
        displayLine("\n Sessione salvata. Arrivederci in palestra!");
    }
}
