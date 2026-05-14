package org.dpt.domain.catalog.machine.dto;

/**
 * Data Transfer Object (DTO) per la registrazione di un nuovo macchinario.
 * -
 * Trasporta i dati minimi necessari per l'inserimento di un macchinario nel catalogo.
 * L'utilizzo dei Java Records garantisce che i dati acquisiti dall'UI non vengano
 * alterati durante il passaggio verso il DAO.
 * 
 * @param name Nome del macchinario.
 * @param description Note descrittive o istruzioni d'uso.
 */
public record MachineCreationDTO(
    String name,
    String description
) {}
