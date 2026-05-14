package org.dpt.domain.catalog.machine.model;

/**
 * Modello di dominio che rappresenta un'attrezzatura o Macchinario della palestra.
 * -
 * Mappa la tabella 'MACCHINARIO' del database MariaDB. Utilizzato per il tracking
 * dei macchinari della palestra e per associare gli esercizi alle rispettive postazioni.
 * 
 * @param id Identificativo univoco del macchinario.
 * @param ownerId Riferimento al Proprietario che gestisce il macchinario.
 * @param name Nome commerciale o descrittivo del macchinario (es. "Leg Press").
 * @param description Caratteristiche tecniche o note sulla manutenzione.
 * @param active Stato di operatività (false se il macchinario è fuori servizio o rimosso).
 */
public record Machine(
        int id,
        int ownerId,
        String name,
        String description,
        boolean active
) {
}
