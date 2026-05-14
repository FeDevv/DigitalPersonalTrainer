package org.dpt.domain.workout.session.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Modello di dominio che rappresenta una specifica Sessione di Allenamento.
 * -
 * Mappa la tabella 'SESSIONE' del database. Una sessione rappresenta l'istanza 
 * temporale di un workout eseguito da un cliente basandosi su una determinata scheda.
 * Registra i metadati temporali e lo stato di avanzamento complessivo dell'allenamento.
 * 
 * @param id Identificativo univoco della sessione.
 * @param sheetId Riferimento alla scheda utilizzata per la sessione.
 * @param date Data di esecuzione del workout.
 * @param startTime Orario di inizio della sessione.
 * @param endTime Orario di chiusura della sessione (null se ancora in corso).
 * @param completionPercentage Percentuale di serie completate rispetto al totale previsto.
 */
public record WorkoutSession(
        int id,
        int sheetId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        int completionPercentage
) {
}
