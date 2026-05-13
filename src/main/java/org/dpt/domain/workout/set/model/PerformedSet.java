package org.dpt.domain.workout.set.model;

/**
 * Modello di dominio che rappresenta una singola serie eseguita durante un workout.
 * -
 * Mappa l'entità debole e la tabella 'SERIE_ESEGUITA'. Ogni istanza tiene traccia
 * del carico sollevato e dello stato di completamento per una specifica serie
 * di un esercizio all'interno di una sessione attiva.
 * 
 * @param sessionId Riferimento alla sessione di allenamento corrente.
 * @param exerciseId Riferimento all'esercizio eseguito.
 * @param setNumber Numero progressivo della serie per quell'esercizio.
 * @param weight Carico effettivo sollevato (in kg), può essere null se non ancora inserito.
 * @param completed Flag booleano che indica se la serie è stata portata a termine.
 */
public record PerformedSet(
        int sessionId,
        int exerciseId,
        int setNumber,
        Double weight,
        boolean completed
) {
}
