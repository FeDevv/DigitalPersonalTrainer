package org.dpt.shared.catalog.exercises.model;

/**
 * Rappresenta un esercizio nel catalogo della palestra
 * Mappa la tabella ESERCIZIO
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
