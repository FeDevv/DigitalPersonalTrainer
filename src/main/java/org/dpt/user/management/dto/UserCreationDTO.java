package org.dpt.user.management.dto;

import org.dpt.shared.utils.ValidationUtils;
import org.dpt.exception.ValidationException;

/**
 * Data Transfer Object (DTO) per l'acquisizione delle informazioni anagrafiche dello staff.
 * -
 * Utilizzato per il trasporto atomico dei dati necessari alla creazione di ruoli operativi 
 * (Personal Trainer, Addetti Segreteria). Implementa il principio "Fail-Fast Validation"
 * tramite un costruttore compatto che garantisce l'integrità formale dei dati 
 * (presenza dei campi, validità email) prima che raggiungano lo strato di persistenza.
 * 
 * @param firstName Nome del dipendente.
 * @param lastName Cognome del dipendente.
 * @param email Indirizzo identificativo univoco.
 * @param password Chiave di accesso iniziale.
 */
public record UserCreationDTO(
    String firstName,
    String lastName,
    String email,
    String password
) {
    /**
     * Valida la consistenza dei dati anagrafici.
     * @throws ValidationException Se i campi obbligatori sono vacanti o l'email è malformata.
     */
    public UserCreationDTO {
        if (firstName == null || firstName.isBlank()) throw new ValidationException("Integrità: il nome è un campo obbligatorio.");
        if (lastName == null || lastName.isBlank()) throw new ValidationException("Integrità: il cognome è un campo obbligatorio.");
        ValidationUtils.validateEmail(email);
        if (password == null || password.isBlank()) throw new ValidationException("Sicurezza: la password non può essere vuota.");
    }
}
