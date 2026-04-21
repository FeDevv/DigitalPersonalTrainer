package org.DPT.login.model;

import org.DPT.exception.ValidationException;

/**
 * Record immutabile per il trasporto delle credenziali di accesso.
 */
public record UserCredentials(String email, String password) {
    /**
     * Validazione per assicurarsi che i campi non siano vuoti.
     */
    public UserCredentials {
        if (email == null || email.isBlank()) {
            throw new ValidationException("L'email non può essere vuota.");
        }
        if (password == null || password.isBlank()) {
            throw new ValidationException("La password non può essere vuota.");
        }
    }
}
