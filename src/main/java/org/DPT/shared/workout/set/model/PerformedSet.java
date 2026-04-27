package org.DPT.shared.workout.set.model;

/**
 * Represents an atomic set performed during a session.
 * Weak entity mapping the SERIE_ESEGUITA table.
 */
public record PerformedSet(
        int sessionId,
        int exerciseId,
        int setNumber,
        Double weight,   // Carico_Effettivo
        boolean completed
) {
}
