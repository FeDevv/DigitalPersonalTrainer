package org.dpt.user.client.controller;

import org.dpt.domain.workout.sheet.model.ActiveSheetItem;
import org.dpt.domain.workout.sheet.model.WorkoutSheet;

import java.util.List;

/**
 * Contratto di interfaccia per l'astrazione della UI dell'Area Cliente.
 * -
 * Definisce i metodi necessari per l'interazione con il cliente, rendendo il
 * {@link ClientLogicController} indipendente dalla tecnologia di visualizzazione.
 * Include specifiche per la gestione interattiva del workflow di allenamento 
 * (registrazione serie, carichi, riepiloghi).
 */
public interface ClientUI {
    /** Mostra l'intestazione personalizzata dell'area cliente. */
    void showHeader(String clientName);

    /** Renderizza il menu principale della dashboard cliente. */
    void showMainMenu();

    /** Acquisisce la scelta dell'utente dal menu. */
    int askForChoice();

    // --- VISUALIZZAZIONE SCHEDE ---
    
    /** Mostra i dettagli tecnici di una specifica routine d'allenamento. */
    void showRoutine(String title, List<ActiveSheetItem> routine);

    /** Mostra l'elenco cronologico delle schede associate al cliente. */
    void showSheetHistory(List<WorkoutSheet> history);

    /** Richiede l'inserimento di un ID (es. per la selezione di una scheda). */
    int askForID(String prompt);

    // --- FLUSSO ALLENAMENTO (WORKOUT EXPERIENCE) ---

    /** Notifica l'apertura di una nuova sessione di allenamento. */
    void showWorkoutStart(String sheetName);

    /** Aggiorna la vista sull'esercizio corrente e le relative note. */
    void showExerciseProgress(int currentEx, int totalEx, String exName, String notes);

    /** Visualizza le informazioni sulla serie corrente (numero, obiettivo reps). */
    void showSetProgress(int currentSet, int totalSets, int reps);
    
    /**
     * Richiede l'esito dell'azione sulla serie corrente.
     * @return 1 per Completata, 2 per Salta Serie, 3 per Salta Esercizio, 0 per Termina Allenamento.
     */
    int askSetAction();
    
    /**
     * Richiede il peso utilizzato per l'esecuzione della serie.
     * @return Il valore del carico o null se l'esercizio è a corpo libero o non specificato.
     */
    Double askForWeight();

    /** Gestisce la visualizzazione e l'attesa del tempo di recupero. */
    void showRestTimer(int seconds);

    /** Mostra il report finale di fine sessione con statistiche di completamento. */
    void showWorkoutSummary(int completedSets, int totalSets, int percentage);

    /** Segnala un errore all'utente. */
    void reportError(String message);

    /** Segnala un'operazione conclusa con successo. */
    void reportSuccess(String message);

    /** Mostra un messaggio informativo di sistema. */
    void reportInfo(String message);

    /** Notifica l'avvenuto logout. */
    void reportGoodbye();
}
