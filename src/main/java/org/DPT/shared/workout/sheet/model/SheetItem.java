package org.DPT.shared.workout.sheet.model;

/**
 * Rappresenta un esercizio all'interno di una scheda di allenamento.
 * Serve a mappare la tabella COMPOSTA.
 */
public record SheetItem(
        int sheetId,
        int exerciseId,
        int restTime,           // Recupero (in seconds)
        String executionNotes,  // Note_Esecuzione
        int expectedSets,       // Serie_Previste
        int expectedReps        // Ripetizioni_Previste
) {
}
