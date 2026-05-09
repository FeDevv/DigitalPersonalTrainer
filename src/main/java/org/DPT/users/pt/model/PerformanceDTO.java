package org.DPT.users.pt.model;

import java.time.LocalDate;

/**
 * Rappresenta una riga del report prestazioni per il PT.
 * Include sia i dettagli della singola sessione che il totale aggregato per il cliente.
 */
public record PerformanceDTO(
        String clientName,
        int totalWorkouts,        // Numero totale di allenamenti nel periodo
        LocalDate date,
        int duration,
        int completionPercentage
) {}
