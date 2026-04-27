package org.DPT.shared.workout.sheet.model;

import java.time.LocalDate;

/**
 * Represents a workout sheet created by a PT for a client.
 * Maps to the SCHEDA table.
 */
public record WorkoutSheet(
        int id,
        int ptId,
        int clientId,
        LocalDate creationDate,
        String title,
        boolean active,
        int totalExpectedSets
) {
}
