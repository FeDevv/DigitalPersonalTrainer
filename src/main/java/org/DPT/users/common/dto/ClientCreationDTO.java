package org.DPT.users.common.dto;

import java.time.LocalDate;

/**
 * DTO per la creazione di un Cliente.
 * Include i dati base dell'utente più i dati specifici del cliente.
 */
public record ClientCreationDTO(
    UserCreationDTO userBase,
    String fiscalCode,
    String address,
    LocalDate birthDate
) {}
