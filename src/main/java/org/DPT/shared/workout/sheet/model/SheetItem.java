package org.DPT.shared.workout.sheet.model;

/**
 * Represents an exercise entry within a workout sheet.
 * Maps to the COMPOSTA table.
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
