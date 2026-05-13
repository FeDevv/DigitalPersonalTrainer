package org.dpt.domain.catalog.exercise.model;

/**
 * Modello di dominio che rappresenta un Esercizio all'interno del catalogo.
 * -
 * Mappa fedelmente la tabella 'ESERCIZIO' del database MariaDB. Utilizza un Java Record 
 * per garantire l'immutabilità dei dati estratti dal DB, fungendo da Read-Model 
 * atomico all'interno dell'applicazione.
 * 
 * @param id Identificativo univoco (Codice_Esercizio).
 * @param ownerId Riferimento al Proprietario che ha inserito l'esercizio.
 * @param machineId ID del macchinario associato (opzionale se a corpo libero).
 * @param name Denominazione dell'esercizio (es. "Panca Piana").
 * @param description Istruzioni tecniche sull'esecuzione.
 * @param bodyweight Flag booleano: true se l'esercizio è a corpo libero (senza macchinario).
 * @param active Stato di visibilità nel catalogo (Soft-delete).
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
