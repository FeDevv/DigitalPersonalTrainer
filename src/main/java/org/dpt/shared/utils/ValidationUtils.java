package org.dpt.shared.utils;

import org.dpt.exception.ValidationException;

/**
 * Fornitore centralizzato di logiche di validazione formale e di dominio.
 * -
 * Questa utility implementa il principio "Fail-Fast Validation": i controlli vengono
 * eseguiti nel punto più vicino possibile all'ingresso del dato (es. nei costruttori 
 * dei DTO/Record), garantendo che nessun dato inconsistente circoli nei layer 
 * interni del sistema (DAO o Business Logic).
 * -
 * Centralizzando le espressioni regolari e i vincoli di dominio, si assicura
 * che le regole di business siano uniformi in tutta l'applicazione.
 */
public class ValidationUtils {

    /** Impedisce l'istanziazione di una classe puramente utility. */
    private ValidationUtils() {
        throw new IllegalStateException("Utility class: non istanziabile.");
    }

    /** Espressione regolare conforme agli standard comuni per la validazione sintattica delle email. */
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    /**
     * Verifica la validità sintattica di un indirizzo email.
     * 
     * @param email La stringa da sottoporre a scansione.
     * @return L'email stessa in caso di successo (permette l'uso in costruttori compatti).
     * @throws ValidationException Se l'input è nullo, vuoto o non conforme alla regex.
     */
    public static String validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Integrità: l'indirizzo email è un campo obbligatorio.");
        }
        if (!email.matches(EMAIL_REGEX)) {
            throw new ValidationException("Formato non valido: '" + email + "' non è un indirizzo email sintatticamente corretto.");
        }
        return email;
    }
}
