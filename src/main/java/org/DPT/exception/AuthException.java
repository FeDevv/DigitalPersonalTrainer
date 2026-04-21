package org.DPT.exception;

/**
 * Eccezione lanciata in caso di fallimento autenticazione o permessi negati.
 */
public class AuthException extends DPTException {
    public AuthException(String message) {
        super(message);
    }
}
