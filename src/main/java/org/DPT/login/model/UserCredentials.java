package org.DPT.login.model;

import org.DPT.auth.Role;
import org.DPT.exception.ValidationException;

/**
 * Record immutabile per il trasporto delle credenziali di accesso.
 * Include il ruolo scelto dall'utente per una ricerca mirata nel database.
 */
public record UserCredentials(String email, String password, Role role) {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    /**
     * Validazione per assicurarsi che tutti i campi siano presenti.
     */
    public UserCredentials {
        if (email == null || email.isBlank()) {
            throw new ValidationException("L'email non può essere vuota.");
        }
        if (!email.matches(EMAIL_REGEX)) {
            throw new ValidationException("Il formato dell'email non è valido.");
        }
        if (password == null || password.isBlank()) {
            throw new ValidationException("La password non può essere vuota.");
        }
        if (role == null || role == Role.LOGIN) {
            throw new ValidationException("È necessario selezionare un ruolo utente valido.");
        }
    }
}
