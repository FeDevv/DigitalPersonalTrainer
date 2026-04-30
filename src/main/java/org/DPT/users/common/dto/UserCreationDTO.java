package org.DPT.users.common.dto;

import org.DPT.shared.utils.ValidationUtils;
import org.DPT.exception.ValidationException;

/**
 * DTO per la creazione di un utente base (Staff: PT, Addetti).
 */
public record UserCreationDTO(
    String firstName,
    String lastName,
    String email,
    String password
) {
    public UserCreationDTO {
        if (firstName == null || firstName.isBlank()) throw new ValidationException("Il nome è obbligatorio.");
        if (lastName == null || lastName.isBlank()) throw new ValidationException("Il cognome è obbligatorio.");
        ValidationUtils.validateEmail(email);
        if (password == null || password.isBlank()) throw new ValidationException("La password è obbligatoria.");
    }
}
