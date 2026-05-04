package org.DPT.shared.workout.sheet.model;

/**
 * Rappresenta un elemento dettagliato della scheda attiva per il cliente.
 * Mappa i risultati della vista 'vw_scheda_attiva_cliente'.
 */
public record ActiveSheetItem(
        int clientId,
        int sheetId,
        String sheetName,
        int exerciseId,
        String exerciseName,
        int expectedSets,
        int expectedReps,
        int restTime,
        String executionNotes,
        boolean bodyweight
) {
}
