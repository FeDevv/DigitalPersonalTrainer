package org.dpt.shared.catalog.macchinari.dto;

/**
 * DTO per l'inserimento di un nuovo macchinario nel catalogo.
 */
public record MachineCreationDTO(
    String name,
    String description
) {}
