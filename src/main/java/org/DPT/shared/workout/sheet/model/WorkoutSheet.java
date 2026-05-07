package org.DPT.shared.workout.sheet.model;

import java.time.LocalDate;

/**
 * Rappresenta una scheda creata da un PT per un cliente
 * Mappa la tabella SCHEDA
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
