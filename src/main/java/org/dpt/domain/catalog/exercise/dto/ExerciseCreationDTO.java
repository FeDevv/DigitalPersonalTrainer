package org.dpt.domain.catalog.exercise.dto;

/**
 * Data Transfer Object (DTO) per l'acquisizione dei dati di un nuovo esercizio.
 * -
 * Utilizzato per trasportare l'intento di creazione dalla UI al DAO. La separazione
 * dal modello di dominio (Exercise) garantisce che i dati di input siano isolati
 * dalle entità dotate di identità (ID), seguendo i principi di Clean Architecture.
 * 
 * @param name Denominazione univoca dell'esercizio.
 * @param description Istruzioni operative.
 * @param isBodyweight true se l'esercizio non richiede macchinari.
 * @param machineId ID del macchinario (opzionale).
 */
public record ExerciseCreationDTO(
    String name,
    String description,
    boolean isBodyweight,
    Integer machineId
) {}
