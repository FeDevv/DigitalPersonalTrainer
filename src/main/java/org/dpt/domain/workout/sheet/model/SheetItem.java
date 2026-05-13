package org.dpt.domain.workout.sheet.model;

/**
 * Modello di scrittura per l'associazione tra Scheda ed Esercizio.
 * 
 * Mappa la tabella di relazione 'COMPOSTA'. Viene utilizzato principalmente in 
 * fase di redazione della scheda per definire i parametri tecnici di un singolo 
 * esercizio inserito nel piano.
 * 
 * @param sheetId Riferimento alla scheda di appartenenza.
 * @param exerciseId Riferimento all'esercizio da eseguire.
 * @param restTime Tempo di recupero tra le serie (espresso in secondi).
 * @param executionNotes Suggerimenti o vincoli tecnici per l'esecuzione.
 * @param expectedSets Numero di serie totali da eseguire.
 * @param expectedReps Numero di ripetizioni target per ogni serie.
 */
public record SheetItem(
        int sheetId,
        int exerciseId,
        int restTime,
        String executionNotes,
        int expectedSets,
        int expectedReps
) {
}
