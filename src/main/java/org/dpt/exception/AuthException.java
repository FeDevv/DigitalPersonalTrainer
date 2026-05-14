package org.dpt.exception;

/**
 * Eccezione specializzata per la segnalazione di violazioni autorizzative o fallimenti di autenticazione.
 * -
 * Lanciata quando le credenziali fornite non sono valide, quando un account 
 * risulta disattivato o quando si tenta di accedere a risorse non permesse 
 * dal ruolo corrente.
 */
public class AuthException extends DPTException {
    public AuthException(String message) {
        super(message);
    }
}
