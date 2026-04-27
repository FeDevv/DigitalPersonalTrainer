package org.DPT.shared.catalog.esercizi.model;

/**
 * Represents an exercise in the gym's catalog.
 * Maps to the ESERCIZIO table.
 */
public record Exercise(
        int id,
        int ownerId,
        Integer machineId,
        String name,
        String description,
        boolean bodyweight,
        boolean active
) {
}
