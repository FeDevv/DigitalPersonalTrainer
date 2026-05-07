package org.DPT.shared.workout.set.model;

/**
 * Rappresenta un set (atomico) eseguito durante una sessione.
 * mappa l'entità debole (e tabella) SERIE_ESEGUITA
 */
public record PerformedSet(
        int sessionId,
        int exerciseId,
        int setNumber,
        Double weight,   // Carico_Effettivo
        boolean completed
) {
}
