package org.dpt.domain.workout.sheet.model;

import java.time.LocalDate;

/**
 * Modello di dominio che rappresenta una Scheda di Allenamento (Workout Plan).
 * 
 * Mappa la tabella 'SCHEDA' del database. La scheda funge da contenitore logico
 * per un insieme di esercizi (SheetItem) assegnati a un cliente da un PT.
 * 
 * @param id Identificativo univoco della scheda.
 * @param ptId Riferimento al Personal Trainer che ha redatto il piano.
 * @param clientId Riferimento al Cliente a cui è destinato l'allenamento.
 * @param creationDate Data di emissione del documento.
 * @param title Denominazione della scheda (es. "Ipertrofia Fase 1").
 * @param active Indica se la scheda è attualmente quella operativa per il cliente.
 * @param totalExpectedSets Conteggio totale delle serie previste (calcolato via trigger).
 */
public record WorkoutSheet(
        int id,
        int ptId,
        int clientId,
        LocalDate creationDate,
        String title,
        boolean active,
        int totalExpectedSets
) {
}
