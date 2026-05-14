package org.dpt.domain.workout.sheet.model;

/**
 * Modello di lettura denormalizzato per la visualizzazione dell'allenamento.
 * -
 * Mappa i risultati della vista database 'vw_scheda_attiva_cliente'.
 * Implementa un approccio ispirato al pattern CQRS (Command Query Responsibility Segregation):
 * a differenza di SheetItem, questo modello include informazioni testuali arricchite
 * (nomi esercizi, flag corpo libero) necessarie alla UI per guidare il cliente,
 * evitando join complessi o query multiple sul lato Java.
 */
public record ActiveSheetItem(
        int clientId,
        int sheetId,
        String sheetName,
        int exerciseId,
        String exerciseName,
        int expectedSets,
        int expectedReps,
        int restTime,
        String executionNotes,
        boolean bodyweight
) {
}
