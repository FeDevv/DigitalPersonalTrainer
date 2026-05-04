package org.DPT.users.client.view;

import org.DPT.shared.ui.BaseCLIView;
import org.DPT.shared.workout.sheet.model.ActiveSheetItem;
import org.DPT.shared.workout.sheet.model.WorkoutSheet;

import java.util.List;

public class ClientCLIView extends BaseCLIView {

    public void displayClientHeader(String name) {
        displayHeader("Area Atleta - Benvenuto " + name);
    }

    public void displayMainMenu() {
        displaySectionTitle("Dashboard");
        displayLine("1. Inizia Allenamento (Scheda Attiva)");
        displayLine("2. Visualizza Routine Scheda Attiva");
        displayLine("3. Visualizza Storico Schede");
        displayLine("0. Logout");
    }

    public void displayActiveRoutine(List<ActiveSheetItem> routine) {
        displaySectionTitle("Tua Routine Corrente");
        if (routine.isEmpty()) {
            displayLine("Non hai ancora una scheda attiva. Chiedi al tuo PT!");
            return;
        }
        System.out.printf("%-25s | %-6s | %-6s | %-10s%n", "Esercizio", "Serie", "Reps", "Recupero");
        displayLine("------------------------------------------------------------");
        for (ActiveSheetItem item : routine) {
            System.out.printf("%-25s | %-6d | %-6d | %-10s%n", 
                    item.exerciseName(), item.expectedSets(), item.expectedReps(), item.restTime() + "s");
        }
    }

    public void displaySheetHistory(List<WorkoutSheet> history) {
        displaySectionTitle("Storico Tue Schede");
        if (history.isEmpty()) {
            displayLine("Nessuna scheda nel tuo storico.");
            return;
        }
        System.out.printf("%-20s | %-12s | %-10s%n", "Titolo", "Data", "Stato");
        displayLine("------------------------------------------------------------");
        for (WorkoutSheet s : history) {
            System.out.printf("%-20s | %-12s | %-10s%n", 
                    s.title(), s.creationDate(), s.active() ? "ATTIVA" : "ARCHIVIATA");
        }
    }

    // --- WORKOUT EXPERIENCE ---

    public void displayWorkoutStart(String sheetName) {
        displaySectionTitle("Allenamento Avviato: " + sheetName);
        displayLine("Buon allenamento! Ricorda: ogni serie conta.");
    }

    public void displayExerciseHeader(int current, int total, String name, String notes) {
        displayEmptyLine();
        displayLine("========================================");
        displayLine(String.format("ESERCIZIO %d di %d: %s", current, total, name.toUpperCase()));
        if (notes != null && !notes.isBlank()) {
            displayLine("Note PT: " + notes);
        }
        displayLine("========================================");
    }

    public void displaySetInfo(int current, int total, int reps, int rest) {
        String progressBar = renderProgressBar(current, total);
        displayLine(String.format("SERIE %d/%d %s | Obiettivo: %d reps", current, total, progressBar, reps));
    }

    public void displaySetMenu() {
        displayLine("\nAzioni serie:");
        displayLine("1. [Fatto] Serie completata");
        displayLine("2. [Salta] Questa serie");
        displayLine("3. [Salta] Intero esercizio");
        displayLine("0. [Termina] Chiudi allenamento");
    }

    public void displayRestTimer(int seconds) {
        displayLine("\n>>> RECUPERO: " + seconds + "s <<<");
        displayLine("(In una GUI qui vedresti un countdown...)");
        displayLine("Premi invio quando sei pronto per la prossima serie.");
    }

    public void displayWorkoutSummary(int completed, int total, int percentage) {
        displaySectionTitle("Allenamento Terminato");
        displayLine("Serie completate: " + completed + " su " + total);
        displayLine("Percentuale completamento: " + percentage + "%");
        displayLine(renderProgressBar(percentage / 10, 10));
        
        if (percentage == 100) displayLine("Ottimo lavoro! Sessione perfetta.");
        else if (percentage > 70) displayLine("Buona sessione, continua così!");
        else displayLine("Sessione completata. Ogni passo conta!");
    }

    private String renderProgressBar(int current, int total) {
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < total; i++) {
            if (i < current) bar.append("#");
            else bar.append("-");
        }
        bar.append("]");
        return bar.toString();
    }

    public void displayGoodbye() {
        displayLine("\nSessione chiusa. Torna presto ad allenarti!");
    }
}
