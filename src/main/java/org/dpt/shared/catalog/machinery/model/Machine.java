package org.dpt.shared.catalog.machinery.model;

/**
 * Rappresenta un macchinario della palestra.
 * Mappa la tabella MACCHINARIO
 */
public record Machine(
        int id,
        int ownerId,
        String name,
        String description,
        boolean active
) {
}
