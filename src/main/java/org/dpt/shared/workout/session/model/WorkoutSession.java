package org.dpt.shared.workout.session.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Rappresenta una sessione di allenamento di un cliente.
 * Mappa la tabella SESSIONE.
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
