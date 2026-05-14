package org.dpt.user.client.view;

import org.dpt.shared.mvc.AbstractCLIView;
import org.dpt.domain.workout.sheet.model.ActiveSheetItem;
import org.dpt.domain.workout.sheet.model.WorkoutSheet;

import java.util.ArrayList;
import java.util.List;

/**
 * Componente di visualizzazione CLI per l'Area Cliente.
 * -
 * Implementa la logica di rendering per la dashboard del cliente e l'esperienza
 * interattiva dell'allenamento. Include componenti grafiche testuali come:
 * <ul>
 *   <li><b>Progress Bar:</b> Visualizzazione grafica del completamento sessione.</li>
 *   <li><b>Tabellazione Dati:</b> Rendering formattato di schede e storico.</li>
 *   <li><b>Box Informativi:</b> Evidenziazione di note tecniche e intestazioni esercizi.</li>
 * </ul>
 */
@SuppressWarnings("java:S106")
public class ClientCLIView extends AbstractCLIView {

    /** Visualizza l'intestazione di benvenuto del modulo cliente. */
    public void displayClientHeader(String name) {
        displayHeader("AREA CLIENTE - Benvenuto/a " + name);
    }

    /** Mostra le opzioni principali della dashboard. */
    public void displayMainMenu() {
        displaySectionTitle("Dashboard");
        displayLine("1. Inizia ALLENAMENTO (Scheda Attiva)");
        displayLine("2. Visualizza ROUTINE Scheda Attiva");
        displayLine("3. Visualizza STORICO SCHEDE");
        displayLine("0. Logout");
    }

    /** Renderizza in formato tabellare i dettagli della routine corrente. */
    public void displayActiveRoutine(String title, List<ActiveSheetItem> routine) {
        renderExerciseTable(title, routine);
    }


    /** Mostra lo storico delle schede in formato tabellare. */
    public void displaySheetHistory(List<WorkoutSheet> history) {
        displaySectionTitle("Storico Tue Schede");
        
        String[] headers = {"ID", "TITOLO SCHEDA", "DATA CREAZIONE", "STATO"};
        List<String[]> rows = new ArrayList<>();
        for (WorkoutSheet s : history) {
            rows.add(new String[]{
                String.valueOf(s.id()),
                s.title(),
                s.creationDate().toString(),
                s.active() ? "ATTIVA" : "ARCHIVIATA"
            });
        }
        
        renderTable(headers, rows, new int[]{5, 25, 20, 12});
    }

    // --- WORKOUT EXPERIENCE RENDERING ---

    /** Visualizza un messaggio motivazionale all'avvio dell'allenamento. */
    public void displayWorkoutStart(String sheetName) {
        displaySectionTitle("Allenamento Avviato");
        displayLine("Scheda: " + sheetName);
        displayLine("Buon allenamento! Ogni serie ti avvicina al tuo obiettivo.");
    }

    /** 
     * Crea un box grafico per evidenziare l'esercizio corrente. 
     * Include le note d'esecuzione fornite dal PT se presenti.
     */
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

    /** Mostra la serie corrente con una progress bar visuale. */
    public void displaySetInfo(int current, int total, int reps) {
        String progressBar = renderProgressBar(current, total);
        displayLine(String.format("SERIE %d/%d %s | Obiettivo: %d reps", current, total, progressBar, reps));
    }

    /** Menu di scelta rapida durante l'allenamento. */
    public void displaySetMenu() {
        displaySectionTitle("Azioni");
        displayLine("1. [Fatto] Serie completata");
        displayLine("2. [Salta] Questa serie");
        displayLine("3. [Salta] Intero esercizio");
        displayLine("0. [Termina] Chiudi allenamento");
    }

    /** Evidenzia il periodo di riposo tra le serie. */
    public void displayRestTimer(int seconds) {
        displayLine("\n >>> RECUPERO: " + seconds + "s <<<");
        displayLine(" (Premi Invio per la prossima serie)");
    }

    /** 
     * Genera un report finale della sessione. 
     * Include una valutazione qualitativa basata sulla percentuale di completamento.
     */
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

    /** Helper per il rendering di una barra di progresso testuale. */
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
