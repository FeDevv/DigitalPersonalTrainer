package org.DPT.shared.catalog.esercizi.dto;

/**
 * DTO per l'inserimento di un nuovo esercizio nel catalogo.
 */
public record ExerciseCreationDTO(
    String name,
    String description,
    boolean isBodyweight,
    Integer machineId
) {}
