package org.dpt.auth.login.model;

import org.dpt.auth.Role;
import org.dpt.exception.ValidationException;
import org.dpt.shared.utils.ValidationUtils;

/**
 * Oggetto atomico per il trasporto delle credenziali di accesso durante l'handshake.
 * -
 * Implementato come Java Record per garantire immutabilità e concisione.
 * Utilizza un costruttore compatto per implementare la "Fail-Fast Validation":
 * le credenziali vengono validate formalmente all'istante della creazione, 
 * impedendo che dati malformati (es. email non valide) raggiungano il DAO.
 * 
 * @param email L'indirizzo identificativo dell'utente.
 * @param password La chiave segreta di accesso.
 * @param role Il ruolo con cui l'utente intende autenticarsi.
 */
public record UserCredentials(String email, String password, Role role) {

    /**
     * Costruttore compatto per la validazione automatica dei dati di input.
     * @throws ValidationException Se l'email è malformata o i campi sono vuoti.
     */
    public UserCredentials {
        ValidationUtils.validateEmail(email);
        if (password == null || password.isBlank()) {
            throw new ValidationException("Credenziali incomplete: la password non può essere vuota.");
        }
        if (role == null || role == Role.LOGIN) {
            throw new ValidationException("Sicurezza: è necessario specificare un ruolo operativo valido.");
        }
    }
}
