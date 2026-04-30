package org.DPT.users.common.dto;

import org.DPT.exception.ValidationException;
import java.time.LocalDate;

/**
 * DTO per la creazione di un cliente.
 * Include i dati base dell'utente e quelli specifici del cliente.
 */
public record ClientCreationDTO(
    UserCreationDTO userBase,
    String fiscalCode,
    String address,
    LocalDate birthDate
) {
    public ClientCreationDTO {
        if (userBase == null) throw new ValidationException("I dati base dell'utente sono obbligatori.");
        if (fiscalCode == null || fiscalCode.length() != 16) throw new ValidationException("Il codice fiscale deve essere di 16 caratteri.");
        if (birthDate == null || birthDate.isAfter(LocalDate.now().minusYears(14))) {
            throw new ValidationException("Il cliente deve avere almeno 14 anni.");
        }
    }
}
