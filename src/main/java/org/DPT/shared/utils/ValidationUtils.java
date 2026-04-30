package org.DPT.shared.utils;

import org.DPT.exception.ValidationException;

/**
 * Utility class per la validazione dei dati di input.
 * Segue il principio DRY per centralizzare le logiche di controllo comuni.
 */
public class ValidationUtils {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    /**
     * Valida il formato di un'email.
     * @param email La stringa da validare.
     * @return La stringa stessa se valida.
     * @throws ValidationException se l'email è null, vuota o non valida.
     */
    public static String validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("L'email non può essere vuota.");
        }
        if (!email.matches(EMAIL_REGEX)) {
            throw new ValidationException("Il formato dell'email non è valido.");
        }
        return email;
    }
}
