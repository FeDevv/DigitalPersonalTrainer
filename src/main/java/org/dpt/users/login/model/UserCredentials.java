package org.dpt.users.login.model;

import org.dpt.shared.auth.Role;
import org.dpt.exception.ValidationException;
import org.dpt.shared.utils.ValidationUtils;

/**
 * Record immutabile per il trasporto delle credenziali di accesso.
 * Include il ruolo scelto dall'utente per una ricerca mirata nel database.
 */
public record UserCredentials(String email, String password, Role role) {

    /**
     * Validazione per assicurarsi che tutti i campi siano presenti.
     */
    public UserCredentials {
        ValidationUtils.validateEmail(email);
        if (password == null || password.isBlank()) {
            throw new ValidationException("La password non può essere vuota.");
        }
        if (role == null || role == Role.LOGIN) {
            throw new ValidationException("È necessario selezionare un ruolo utente valido.");
        }
    }
}
