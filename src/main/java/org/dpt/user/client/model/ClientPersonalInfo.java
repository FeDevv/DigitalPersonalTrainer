package org.dpt.user.client.model;

import java.time.LocalDate;

/**
 * Raggruppa i dati personali specifici di un cliente.
 * Utilizzato per ridurre il numero di parametri nel costruttore del modello Client.
 */
public record ClientPersonalInfo(
        String fiscalCode,
        String address,
        LocalDate birthDate
) {}
