package org.dpt.user.pt.model;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) per il riepilogo delle prestazioni degli atleti.
 * -
 * Mappa i dati provenienti dalla vista database 'vw_prestazioni_pt' aggregati 
 * con informazioni sul volume di allenamento. Viene utilizzato dal modulo PT 
 * per monitorare l'aderenza ai piani di allenamento e i progressi dei propri clienti.
 * 
 * @param clientName Nominativo completo del cliente.
 * @param totalWorkouts Conteggio totale delle sessioni eseguite nel periodo richiesto.
 * @param date Data di esecuzione della specifica sessione monitorata.
 * @param duration Durata effettiva dell'allenamento (in minuti).
 * @param completionPercentage Rapporto tra serie previste e serie effettivamente completate.
 */
public record PerformanceDTO(
        String clientName,
        int totalWorkouts,
        LocalDate date,
        int duration,
        int completionPercentage
) {}
