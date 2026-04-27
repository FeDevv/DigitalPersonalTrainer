package org.DPT.shared.catalog.macchinari.model;

/**
 * Represents a piece of equipment in the gym.
 * Maps to the MACCHINARIO table.
 */
public record Machine(
        int id,
        int ownerId,
        String name,
        String description,
        boolean active
) {
}
