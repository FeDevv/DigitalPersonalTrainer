package org.DPT.exception;

/**
 * Eccezione lanciata quando l'input dell'utente non rispetta i requisiti.
 */
public class ValidationException extends DPTException {
    public ValidationException(String message) {
        super(message);
    }
}
