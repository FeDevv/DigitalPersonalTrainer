package org.DPT.shared.workout.session.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a specific training session performed by a client.
 * Maps to the SESSIONE table.
 */
public record WorkoutSession(
        int id,
        int sheetId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        int completionPercentage
) {
}
